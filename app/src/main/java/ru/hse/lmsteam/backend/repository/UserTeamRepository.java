package ru.hse.lmsteam.backend.repository;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.UUID;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.teams.UserTeam;

public interface UserTeamRepository {
  Flux<User> getMembers(UUID teamId);

  Flux<Team> getUserTeams(UUID userId);

  Flux<UserTeam> getUsersTeams(Collection<UUID> userId);

  Flux<User> getTeammates(UUID memberId);

  Flux<UUID> setUserTeamMemberships(UUID teamId, ImmutableSet<UUID> userIds);
}
