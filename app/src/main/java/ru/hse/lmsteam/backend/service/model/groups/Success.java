package ru.hse.lmsteam.backend.service.model.groups;

public record Success() implements SetUserGroupMembershipResponse {
  @Override
  public boolean success() {
    return true;
  }
}
