package ru.hse.lmsteam.backend.service.model.groups;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.UUID;
import ru.hse.lmsteam.backend.domain.Group;

public record ValidationErrors(
    Optional<ImmutableSet<UUID>> notFoundUserIds,
    Optional<ImmutableMap<UUID, Group>> alreadyMembers)
    implements SetUserGroupMembershipResponse {
  @Override
  public boolean success() {
    return false;
  }
}
