package ru.hse.lmsteam.backend.service.model.auth;

import ru.hse.lmsteam.backend.domain.UserAuth;

public record InternalAuthorizationResult(UserAuth user) implements AuthorizationResult {
  @Override
  public boolean isAuthorized() {
    return true;
  }
}
