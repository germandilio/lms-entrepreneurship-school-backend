package ru.hse.lmsteam.backend.service.teams;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;

public interface TeamManager {
  Mono<Team> findById(UUID id);

  Mono<Map<UUID, Team>> findByIds(Collection<UUID> id);

  Flux<Team> findByMember(UUID memberId);

  /**
   * Find teammates of all groups that he is a member including himself.
   *
   * @param memberId member id
   * @return If member role is LEARNER, return all teammates for the group (including trackers). If
   *     member role is TRACKER, return all team members of all the groups that he tracks (including
   *     himself and other trackers). If member is ADMIN - no team members are returned.
   */
  Flux<User> findTeammates(UUID memberId);

  Mono<Tuple2<Team, SetUserTeamMembershipResponse>> update(Team team, ImmutableSet<UUID> memberIds);

  Mono<Tuple2<Team, SetUserTeamMembershipResponse>> create(Team team, ImmutableSet<UUID> memberIds);

  Mono<Long> delete(UUID teamId);

  Flux<User> getTeamMembers(UUID teamId);

  Mono<SetUserTeamMembershipResponse> updateTeamMembers(UUID teamId, ImmutableSet<UUID> userIds);

  Mono<SetUserTeamMembershipResponse> validateTeamMembers(UUID teamId, ImmutableSet<UUID> userIds);

  Mono<Page<Team>> findAll(TeamsFilterOptions filterOptions, Pageable pageable);
}
