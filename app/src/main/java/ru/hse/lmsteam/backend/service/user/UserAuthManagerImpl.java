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
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserAuth;
import ru.hse.lmsteam.backend.repository.UserAuthRepository;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.jwt.TokenManager;
import ru.hse.lmsteam.backend.service.mail.SetNewPasswordEmailSender;
import ru.hse.lmsteam.backend.service.model.auth.*;
import ru.hse.lmsteam.backend.service.validation.PasswordValidator;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthManagerImpl implements UserAuthManager, UserAuthInternal {
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
            Mono.error(
                new BusinessLogicNotFoundException("User with login " + login + " not found")))
        .map(auth -> doAuthenticate(auth).apply(login, password))
        .flatMap(withAuthToken(login));
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<? extends AuthorizationResult> authorize(String token) {
    return tokenManager
        .getUserId(token)
        .map(id -> userAuthRepository.findByUserId(id, false))
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

  @Transactional(readOnly = true)
  @Override
  public Mono<? extends AuthorizationResult> tryRetrieveUser(String token) {
    return tokenManager
        .getUserId(token)
        .map(id -> userAuthRepository.findByUserId(id, false))
        .orElse(Mono.empty())
        .map(
            userAuth -> {
              if (!userAuth.isDeleted()) {
                return new InternalAuthorizationResult(userAuth);
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
            Mono.error(
                new BusinessLogicNotFoundException("User with login " + login + " not found")))
        .flatMap(
            userAuth ->
                doAuthenticate(userAuth)
                    .apply(login, oldPassword)
                    .<UserAuth>handle(
                        (r, sink) -> {
                          if (r.success()) {
                            sink.next(userAuth);
                          } else {
                            sink.error(new ValidationException(r.message()));
                          }
                        }))
        .flatMap(
            userAuth ->
                userAuthRepository.update(
                    userAuth.withPassword(getPasswordForStoring(newPassword))))
        .flatMap(
            userAuth -> {
              log.info("Password changed for user with login {}", login);
              return userRepository
                  .findById(userAuth.userId())
                  .flatMap(
                      user ->
                          withAuthToken(login)
                              .apply(Mono.just(AuthResult.success(user, userAuth.role()))));
            })
        .onErrorResume(
            ValidationException.class, exc -> Mono.just(AuthResult.failure(exc.getMessage())));
  }

  @Override
  public Mono<AuthResult> setPassword(UUID token, String newPassword) {
    return userAuthRepository
        .findByToken(token)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("User not found")))
        .<UserAuth>handle(
            (userAuth, sink) -> {
              if (userAuth.password() != null) {
                sink.error(
                    new BusinessLogicExpectationFailedException("Password already set for user"));
                return;
              }
              if (userAuth.passwordResetToken() == null
                  || !userAuth.passwordResetToken().equals(token)) {
                sink.error(new BusinessLogicExpectationFailedException("Provided invalid token"));
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
        .flatMap(
            userAuth -> {
              log.info("Password set for user with login {}", userAuth.login());
              return userRepository
                  .findById(userAuth.userId())
                  .flatMap(
                      user ->
                          withAuthToken(userAuth.login())
                              .apply(Mono.just(AuthResult.success(user, userAuth.role()))));
            })
        .onErrorResume(
            ValidationException.class, exc -> Mono.just(AuthResult.failure(exc.getMessage())));
  }

  @Transactional
  @Override
  public Mono<UserAuth> register(User user) {
    if (user == null || user.id() == null) {
      throw new BusinessLogicExpectationFailedException("User or user id is null!");
    }

    // keep password empty (it will be set by user later by setPassword with special
    // passwordResetToken)
    var userAuth =
        new UserAuth(null, user.id(), user.email(), null, UUID.randomUUID(), user.role(), false);

    sendEmailWithToken(user.email(), userAuth.passwordResetToken()).subscribe();

    return userAuthRepository
        .insert(userAuth)
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "UserAuth with the same login already exists!"));
              }

              return Mono.error(exc);
            })
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
        .findByUserId(user.id(), true)
        .flatMap(dbAuth -> doAuthUpdate(user, dbAuth))
        .switchIfEmpty(register(user))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "User with login " + user.email() + " already exists"));
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
            .id(dbAuth.id())
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
          .flatMap(
              auth -> {
                log.info("User auth updated for userId = {}", auth.userId());
                if (!Objects.equals(updatedUserAuth.login(), dbAuth.login())) {
                  // TODO maybe sent different texts in email, (update email or registered)
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

  private BiFunction<String, String, Mono<AuthResult>> doAuthenticate(UserAuth userAuth) {
    return (String login, String password) -> {
      if (userAuth.isDeleted()) {
        return Mono.just(AuthResult.failure("User with login " + login + " is deleted"));
      }
      if (userAuth.password() == null) {
        return Mono.just(
            AuthResult.failure(
                "User with login " + login + " needs to set password before authorizing"));
      }

      if (passwordEncoder.matches(password, userAuth.password())) {
        return userRepository
            .findById(userAuth.userId())
            .map(user -> AuthResult.success(user, userAuth.role()));
      } else {
        return Mono.just(AuthResult.failure("Invalid password"));
      }
    };
  }

  private Function<Mono<AuthResult>, Mono<AuthResult>> withAuthToken(String login) {
    return authR ->
        authR.map(
            authResult -> {
              if (authResult.user().isPresent() && authResult.role().isPresent()) {
                return authResult.withAuthToken(
                    Optional.of(tokenManager.createToken(authResult.user().get().id())));
              } else {
                log.error("Empty userId or role on authResult with login {}", login);
                return authResult;
              }
            });
  }

  private String getPasswordForStoring(String rawPassword) {
    passwordValidator.validate(rawPassword);
    return passwordEncoder.encode(rawPassword);
  }
}
