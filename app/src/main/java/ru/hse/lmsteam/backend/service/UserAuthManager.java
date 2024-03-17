package ru.hse.lmsteam.backend.service;

import java.util.UUID;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserAuth;
import ru.hse.lmsteam.backend.service.model.AuthResult;

public interface UserAuthManager {
  Mono<AuthResult> authenticate(String login, String password);

  Mono<AuthResult> changePassword(String login, String oldPassword, String newPassword);

  Mono<AuthResult> setPassword(String login, UUID token, String newPassword);

  Mono<UserAuth> register(User user);

  /**
   * This method is callback when user is changed. It should update user auth data if needed.
   *
   * @param user updated user
   * @return updated user auth data
   */
  Mono<UserAuth> onUserChanged(User user);

  /**
   * This method is callback when user is deleted. It should update user auth data if needed.
   *
   * @param userId user id
   * @return nothing
   */
  Mono<Void> onUserDeleted(UUID userId);
}
