package ru.hse.lmsteam.backend.service.teams;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;

public interface TeamManager {
  Mono<Team> findById(UUID id);

  Mono<Team> update(Team team);

  Mono<Team> create(Team team);

  Mono<Long> delete(UUID teamId);

  Flux<User> getTeamMembers(UUID teamId);

  Mono<SetUserTeamMembershipResponse> updateTeamMembers(UUID teamId, ImmutableSet<UUID> userIds);

  Mono<SetUserTeamMembershipResponse> validateTeamMembers(UUID teamId, ImmutableSet<UUID> userIds);

  Mono<Page<Team>> findAll(TeamsFilterOptions filterOptions, Pageable pageable);
}
