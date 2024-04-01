package ru.hse.lmsteam.backend.service.jwt;

import java.util.Optional;
import java.util.UUID;

// TODO refactor to store tokens in database (one per user, to be able to revoke them)

public interface TokenManager {
  String createToken(UUID userid);

  /**
   * Checks if the token is valid and retrieves the user id
   *
   * @param token JWT token to validate
   * @return user id if the token is valid, empty otherwise
   */
  Optional<UUID> getUserId(String token);

  /**
   * Checks if the token is valid and belongs to the user
   *
   * @param token JWT token to validate
   * @param userId target user id
   * @return true if the token is valid and belongs to the user
   */
  boolean validate(String token, UUID userId);
}
