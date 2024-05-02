package ru.hse.lmsteam.backend.repository;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;

public interface UserTeamRepository {
  Flux<User> getMembers(UUID teamId);

  Flux<Team> getUserTeams(UUID userId);

  Flux<User> getTeammates(UUID memberId);

  Flux<UUID> setUserTeamMemberships(UUID teamId, ImmutableSet<UUID> userIds);
}
