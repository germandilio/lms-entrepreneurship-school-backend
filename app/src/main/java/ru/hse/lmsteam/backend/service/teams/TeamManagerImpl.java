package ru.hse.lmsteam.backend.service.teams;

import com.google.common.collect.ImmutableSet;
import jakarta.validation.ValidationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.repository.TeamRepository;
import ru.hse.lmsteam.backend.repository.UserTeamRepository;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;
import ru.hse.lmsteam.backend.service.user.UserManager;
import ru.hse.lmsteam.backend.service.validation.TeamValidator;

@RequiredArgsConstructor
@Service
public class TeamManagerImpl implements TeamManager {
  private final TeamValidator teamValidator;
  private final TeamRepository teamRepository;
  private final UserTeamRepository userTeamRepository;
  private final UserManager userManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<Team> findById(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return teamRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<Tuple2<Team, SetUserTeamMembershipResponse>> update(
      final Team team, final ImmutableSet<UUID> memberIds) {
    if (team == null) {
      throw new IllegalArgumentException(
          "Group object is mandatory for update / create operations.");
    }
    teamValidator.validateForSave(team);
    return teamRepository
        .findById(team.id(), true)
        .map(dbGroup -> team.mergeWith(dbGroup, false))
        .flatMap(teamRepository::upsert)
        .flatMap(id -> teamRepository.findById(id, false))
        .zipWhen(group -> updateTeamMembers(group.id(), memberIds))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new ValidationException(
                        "Group with number " + team.number() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Tuple2<Team, SetUserTeamMembershipResponse>> create(
      final Team team, final ImmutableSet<UUID> memberIds) {
    if (team == null) {
      throw new IllegalArgumentException(
          "Group object is mandatory for update / create operations.");
    }
    teamValidator.validateForSave(team);
    return teamRepository
        .upsert(team)
        .flatMap(id -> teamRepository.findById(id, false))
        .zipWhen(group -> updateTeamMembers(group.id(), memberIds))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new ValidationException(
                        "Group with number " + team.number() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Long> delete(final UUID id) {
    if (id == null) {
      return Mono.just(0L);
    }
    return teamRepository.delete(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<User> getTeamMembers(final UUID teamId) {
    return userTeamRepository.getMembers(teamId);
  }

  @Override
  public Mono<SetUserTeamMembershipResponse> updateTeamMembers(
      final UUID teamId, final ImmutableSet<UUID> userIds) {
    return userManager.setUserTeamMemberships(teamId, userIds);
  }

  @Override
  public Mono<SetUserTeamMembershipResponse> validateTeamMembers(
      final UUID teamId, final ImmutableSet<UUID> userIds) {
    return userManager.validateUserTeamMemberships(teamId, userIds);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Team>> findAll(final TeamsFilterOptions filterOptions, final Pageable pageable) {
    if (pageable == null) {
      throw new IllegalArgumentException(
          "Page parameters are mandatory. Please provide Pageable object with specified page params.");
    }
    var options = filterOptions == null ? new TeamsFilterOptions(null) : filterOptions;
    return teamRepository.findAll(options, pageable);
  }
}
