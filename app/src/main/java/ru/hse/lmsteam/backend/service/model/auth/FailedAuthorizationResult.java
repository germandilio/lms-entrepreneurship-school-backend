package ru.hse.lmsteam.backend.service.model.auth;

public record FailedAuthorizationResult() implements AuthorizationResult {
  @Override
  public boolean isAuthorized() {
    return false;
  }
}
