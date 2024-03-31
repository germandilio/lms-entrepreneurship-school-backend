package ru.hse.lmsteam.backend.service.model;

import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import ru.hse.lmsteam.backend.domain.UserRole;

@Builder
public record AuthResult(
    boolean success,
    String message,
    @With Optional<String> authToken,
    Optional<UserRole> role,
    Optional<UUID> userId) {

  public static AuthResult success(UUID userId, UserRole role) {
    return AuthResult.builder()
        .success(true)
        .message("Success")
        .authToken(Optional.empty())
        .role(Optional.of(role))
        .userId(Optional.of(userId))
        .build();
  }

  public static AuthResult failure(String message) {
    return AuthResult.builder()
        .success(false)
        .message(message)
        .authToken(Optional.empty())
        .role(Optional.empty())
        .userId(Optional.empty())
        .build();
  }
}
