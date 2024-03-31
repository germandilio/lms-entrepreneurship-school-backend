package ru.hse.lmsteam.backend.service.jwt;

import java.util.UUID;

// TODO refactor to store tokens in database (one per user, to be able to revoke them)

public interface TokenManager {
  String createToken(UUID userid);

  boolean validate(String token, UUID userId);
}
