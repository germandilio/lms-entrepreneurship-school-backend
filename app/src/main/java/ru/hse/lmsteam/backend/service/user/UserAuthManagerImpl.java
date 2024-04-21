package ru.hse.lmsteam.backend.service.user;

import jakarta.validation.ValidationException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserAuth;
import ru.hse.lmsteam.backend.repository.UserAuthRepository;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.service.jwt.TokenManager;
import ru.hse.lmsteam.backend.service.mail.SetNewPasswordEmailSender;
import ru.hse.lmsteam.backend.service.model.auth.AuthResult;
import ru.hse.lmsteam.backend.service.model.auth.AuthorizationResult;
import ru.hse.lmsteam.backend.service.model.auth.FailedAuthorizationResult;
import ru.hse.lmsteam.backend.service.model.auth.SuccessfulAuthorizationResult;
import ru.hse.lmsteam.backend.service.validation.PasswordValidator;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthManagerImpl implements UserAuthManager {
  private final UserAuthRepository userAuthRepository;
  private final UserRepository userRepository;

  private final PasswordValidator passwordValidator;
  private final PasswordEncoder passwordEncoder;

  private final TokenManager tokenManager;

  private final SetNewPasswordEmailSender setNewPasswordEmailSender;

  @Transactional(readOnly = true)
  @Override
  public Mono<AuthResult> authenticate(String login, String password) {
    return userAuthRepository
        .findByLogin(login, false)
        .switchIfEmpty(
            Mono.error(new ValidationException("User with login " + login + " not found")))
        .map(auth -> doAuthenticate(auth).apply(login, password))
        .map(withAuthToken(login));
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<? extends AuthorizationResult> authorize(String token) {
    return tokenManager
        .getUserId(token)
        .map(id -> userAuthRepository.findById(id, false))
        .orElse(Mono.empty())
        .map(
            userAuth -> {
              if (!userAuth.isDeleted()) {
                return new SuccessfulAuthorizationResult(userAuth.userId(), userAuth.role());
              } else {
                return new FailedAuthorizationResult();
              }
            })
        .switchIfEmpty(Mono.just(new FailedAuthorizationResult()));
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
              var authResult = doAuthenticate(userAuth).apply(login, oldPassword);
              if (!authResult.success()) {
                sink.error(new ValidationException(authResult.message()));
                return;
              }
              sink.next(userAuth);
            })
        .flatMap(
            userAuth ->
                userAuthRepository.update(
                    userAuth.withPassword(getPasswordForStoring(newPassword))))
        .map(
            userAuth -> {
              log.info("Password changed for user with login {}", login);
              return withAuthToken(login)
                  .apply(AuthResult.success(getBlockingUser(userAuth.userId()), userAuth.role()));
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
              if (userAuth.password() != null) {
                sink.error(new ValidationException("Password already set for user"));
                return;
              }
              if (userAuth.passwordResetToken() == null
                  || !userAuth.passwordResetToken().equals(token)) {
                sink.error(new ValidationException("Provided invalid token"));
                return;
              }
              sink.next(userAuth);
            })
        .flatMap(
            userAuth -> {
              var updatedUser =
                  new UserAuth(
                      userAuth.id(),
                      userAuth.userId(),
                      userAuth.login(),
                      getPasswordForStoring(newPassword),
                      null, // to ensure than passwordResetToken can be used only once
                      userAuth.role(),
                      false);
              return userAuthRepository.update(updatedUser);
            })
        .map(
            userAuth -> {
              log.info("Password set for user with login {}", login);
              return withAuthToken(login)
                  .apply(AuthResult.success(getBlockingUser(userAuth.userId()), userAuth.role()));
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

    // keep password empty (it will be set by user later by setPassword with special
    // passwordResetToken)
    var userAuth =
        new UserAuth(null, user.id(), user.email(), null, UUID.randomUUID(), user.role(), false);

    sendEmailWithToken(user.email(), userAuth.passwordResetToken()).subscribe();

    return userAuthRepository
        .insert(userAuth)
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

  @Transactional
  protected Mono<UserAuth> doAuthUpdate(User user, UserAuth dbAuth) {
    var updatedUserAuth =
        UserAuth.builder()
            .userId(user.id())
            .login(user.email())
            .password(dbAuth.password())
            .role(user.role())
            .passwordResetToken(dbAuth.passwordResetToken())
            .isDeleted(user.isDeleted())
            .build();

    if (updatedUserAuth.isDeleted()) {
      return onUserDeleted(user.id()).thenReturn(updatedUserAuth);
    } else {
      return userAuthRepository
          .update(updatedUserAuth)
          .publishOn(Schedulers.boundedElastic())
          .flatMap(
              auth -> {
                log.info("User auth updated for userId = {}", auth.userId());
                if (!Objects.equals(updatedUserAuth.login(), dbAuth.login())
                    && updatedUserAuth.passwordResetToken() != null) {
                  sendEmailWithToken(updatedUserAuth.login(), updatedUserAuth.passwordResetToken())
                      .subscribe();
                }

                return Mono.just(auth);
              });
    }
  }

  private Mono<Void> sendEmailWithToken(String userEmail, UUID token) {
    return Mono.fromFuture(setNewPasswordEmailSender.sendEmail(userEmail, token.toString()));
  }

  private BiFunction<String, String, AuthResult> doAuthenticate(UserAuth userAuth) {
    return (String login, String password) -> {
      if (userAuth.isDeleted()) {
        return AuthResult.failure("User with login " + login + " is deleted");
      }
      if (userAuth.password() == null) {
        return AuthResult.failure(
            "User with login " + login + " needs to set password before authorizing");
      }

      if (passwordEncoder.matches(password, userAuth.password())) {
        return AuthResult.success(getBlockingUser(userAuth.userId()), userAuth.role());
      } else {
        return AuthResult.failure("Invalid password");
      }
    };
  }

  private Function<AuthResult, AuthResult> withAuthToken(String login) {
    return authResult -> {
      if (authResult.user().isPresent() && authResult.role().isPresent()) {
        return authResult.withAuthToken(
            Optional.of(tokenManager.createToken(authResult.user().get().id())));
      } else {
        log.error("Empty userId or role on authResult with login {}", login);
        return authResult;
      }
    };
  }

  private String getPasswordForStoring(String rawPassword) {
    passwordValidator.validate(rawPassword);
    return passwordEncoder.encode(rawPassword);
  }

  /** Unsafe util method for getting userInfo for existing user_id from user_auth module */
  private User getBlockingUser(UUID id) {
    if (id == null) return null;
    return userRepository.findById(id).block();
  }
}
