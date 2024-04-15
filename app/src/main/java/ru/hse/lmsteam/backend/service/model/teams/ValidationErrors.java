package ru.hse.lmsteam.backend.service.model.teams;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.UUID;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;

public record ValidationErrors(
    Optional<ImmutableSet<UUID>> notFoundUserIds,
    Optional<ImmutableMap<User, ImmutableList<Team>>> alreadyMembers)
    implements SetUserTeamMembershipResponse {
  @Override
  public boolean success() {
    return false;
  }
}
