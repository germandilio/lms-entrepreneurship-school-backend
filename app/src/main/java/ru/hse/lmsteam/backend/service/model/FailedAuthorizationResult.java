package ru.hse.lmsteam.backend.service.model;

public record FailedAuthorizationResult() implements AuthorizationResult {
  @Override
  public boolean isAuthorized() {
    return false;
  }
}
