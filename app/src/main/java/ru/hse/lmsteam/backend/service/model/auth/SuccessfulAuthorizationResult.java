package ru.hse.lmsteam.backend.service.model.auth;

import java.util.UUID;
import ru.hse.lmsteam.backend.domain.UserRole;

public record SuccessfulAuthorizationResult(UUID userId, UserRole role)
    implements AuthorizationResult {
  @Override
  public boolean isAuthorized() {
    return true;
  }
}
