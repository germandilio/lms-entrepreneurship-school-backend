package ru.hse.lmsteam.backend.service.model.teams;

public record Success() implements SetUserTeamMembershipResponse {
  @Override
  public boolean success() {
    return true;
  }
}
