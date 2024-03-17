package ru.hse.lmsteam.backend.service;

import jakarta.validation.ValidationException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserAuth;
import ru.hse.lmsteam.backend.repository.UserAuthRepository;
import ru.hse.lmsteam.backend.service.model.AuthResult;
import ru.hse.lmsteam.backend.service.validation.PasswordValidator;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthManagerImpl implements UserAuthManager {
  private final UserAuthRepository userAuthRepository;

  private final PasswordValidator passwordValidator;

  // TODO return real jwt token with tokenManager
  // private final TokenManager tokenManager;
  // TODO password hashing with BCryptPasswordEncoder (?? how to use salt ??)
  // private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  @Override
  public Mono<AuthResult> authenticate(String login, String password) {
    return userAuthRepository
        .findByLogin(login, false)
        .switchIfEmpty(
            Mono.error(new ValidationException("User with login " + login + " not found")))
        .map(userAuth -> doAuthenticate(login, password, userAuth))
        .onErrorResume(
            ValidationException.class, exc -> Mono.just(AuthResult.failure(exc.getMessage())));
  }

  @Transactional
  @Override
  public Mono<AuthResult> changePassword(String login, String oldPassword, String newPassword) {
    return userAuthRepository
        .findByLogin(login, true)
        .switchIfEmpty(
            Mono.error(new ValidationException("User with login " + login + " not found")))
        .<UserAuth>handle(
            (userAuth, sink) -> {
              var authResult = doAuthenticate(login, oldPassword, userAuth);
              if (!authResult.success()) {
                sink.error(new ValidationException(authResult.message()));
                return;
              }
              sink.next(userAuth);
            })
        .flatMap(
            userAuth -> {
              passwordValidator.validate(newPassword);
              // TODO hash password with passwordEncoder
              var updatedPassword = newPassword;
              return userAuthRepository.upsert(userAuth.withPassword(updatedPassword));
            })
        .map(
            userAuth -> {
              log.info("Password changed for user with login {}", login);
              return AuthResult.success(userAuth.userId(), "", userAuth.role());
            })
        .onErrorResume(
            ValidationException.class, exc -> Mono.just(AuthResult.failure(exc.getMessage())));
  }

  @Override
  public Mono<AuthResult> setPassword(String login, UUID token, String newPassword) {
    return userAuthRepository
        .findByLogin(login, true)
        .switchIfEmpty(
            Mono.error(new ValidationException("User with login " + login + " not found")))
        .<UserAuth>handle(
            (userAuth, sink) -> {
              if (userAuth.token() == null || !userAuth.token().equals(token)) {
                sink.error(new ValidationException("Invalid token"));
                return;
              }
              sink.next(userAuth);
            })
        .flatMap(
            userAuth -> {
              passwordValidator.validate(newPassword);
              // TODO hash password with passwordEncoder
              var updatedPassword = newPassword;
              var updatedUser =
                  UserAuth.builder()
                      .userId(userAuth.userId())
                      .login(userAuth.login())
                      .role(userAuth.role())
                      .password(updatedPassword)
                      .isDeleted(false)
                      .build();
              return userAuthRepository.upsert(updatedUser);
            })
        .map(
            userAuth -> {
              log.info("Password set for user with login {}", login);
              return AuthResult.success(userAuth.userId(), "", userAuth.role());
            })
        .onErrorResume(
            ValidationException.class, exc -> Mono.just(AuthResult.failure(exc.getMessage())));
  }

  @Transactional
  @Override
  public Mono<UserAuth> register(User user) {
    if (user == null || user.id() == null) {
      throw new IllegalArgumentException("User or user id is null!");
    }
    // !!! set all fields to avoid double db query here !!!
    var userAuth =
        UserAuth.builder()
            .userId(user.id())
            .login(user.email())
            // keep password empty (it will be set by user later by setPassword with special token)
            .token(UUID.randomUUID())
            .role(user.role())
            .isDeleted(false)
            .build();

    sendEmailWithToken(user.email(), userAuth.token())
        .subscribe(__ -> log.info("Email with token sent to user {}", user.email()));

    return userAuthRepository
        .upsert(userAuth)
        .map(
            auth -> {
              log.info("User auth created for userId = {}", auth.userId());
              return auth;
            });
  }

  @Transactional
  @Override
  public Mono<UserAuth> onUserChanged(User user) {
    return userAuthRepository
        .findById(user.id(), true)
        .flatMap(dbAuth -> doAuthUpdate(user, dbAuth))
        .switchIfEmpty(register(user))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new ValidationException("User with login " + user.email() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Void> onUserDeleted(UUID userId) {
    return userAuthRepository.delete(userId).then();
  }

  protected Mono<UserAuth> doAuthUpdate(User user, UserAuth dbAuth) {
    var updatedUserAuth =
        UserAuth.builder()
            .userId(user.id())
            .login(user.email())
            .password(dbAuth.password())
            .role(user.role())
            .token(dbAuth.token())
            .isDeleted(user.isDeleted())
            .build();

    if (updatedUserAuth.isDeleted()) {
      return onUserDeleted(user.id()).thenReturn(updatedUserAuth);
    } else {
      return userAuthRepository
          .upsert(updatedUserAuth)
          .flatMap(
              auth -> {
                log.info("User auth updated for userId = {}", auth.userId());
                if (!Objects.equals(updatedUserAuth.login(), dbAuth.login())
                    && updatedUserAuth.token() != null) {
                  sendEmailWithToken(updatedUserAuth.login(), updatedUserAuth.token())
                      .subscribe(
                          __ ->
                              log.info(
                                  "Email with token sent to user {}", updatedUserAuth.login()));
                }

                return Mono.just(auth);
              });
    }
  }

  private Mono<Void> sendEmailWithToken(String email, UUID token) {
    // TODO send email with token to user (think about possible errors while sending and proper
    // handling)
    return Mono.empty()
        .onErrorResume(
            exc -> {
              log.error("Error while sending email with token to user {}", email, exc);
              // TODO retry logic
              return Mono.empty();
            })
        .then();
  }

  private AuthResult doAuthenticate(String login, String password, UserAuth userAuth) {
    if (userAuth.isDeleted()) {
      return AuthResult.failure("User with login " + login + " is deleted");
    }
    if (userAuth.password() == null) {
      return AuthResult.failure("User with login " + login + " has no password");
    }

    if (userAuth.password().equals(password) && userAuth.token() == null) {
      return AuthResult.success(userAuth.userId(), "", userAuth.role());
    } else {
      return AuthResult.failure("Invalid password");
    }
  }
}
