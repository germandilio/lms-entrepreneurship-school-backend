package ru.hse.lmsteam.backend.service.model.auth;

import java.util.Optional;
import lombok.Builder;
import lombok.With;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserRole;

@Builder
public record AuthResult(
    boolean success,
    String message,
    @With Optional<String> authToken,
    Optional<UserRole> role,
    Optional<User> user) {

  public static AuthResult success(User user, UserRole role) {
    return AuthResult.builder()
        .success(true)
        .message("Success")
        .authToken(Optional.empty())
        .role(Optional.of(role))
        .user(Optional.of(user))
        .build();
  }

  public static AuthResult failure(String message) {
    return AuthResult.builder()
        .success(false)
        .message(message)
        .authToken(Optional.empty())
        .role(Optional.empty())
        .user(Optional.empty())
        .build();
  }
}
