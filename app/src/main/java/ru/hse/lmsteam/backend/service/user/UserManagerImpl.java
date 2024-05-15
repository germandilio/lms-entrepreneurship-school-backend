package ru.hse.lmsteam.backend.service.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.repository.UserTeamRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.Success;
import ru.hse.lmsteam.backend.service.model.teams.ValidationErrors;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.backend.service.teams.UserTeamManager;
import ru.hse.lmsteam.backend.service.validation.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
  private final UserRepository userRepository;
  private final UserTeamRepository userTeamRepository;
  private final UserTeamManager userTeamManager;
  private final UserValidator userValidator;

  private final UserAuthManager userAuthManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<User> findById(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.findById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Map<UUID, User>> findByIds(Collection<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.just(Map.of());
    }

    return userRepository.findByIds(ids).collectMap(User::id);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<User> findTeamMembers(UUID teamId) {
    if (teamId == null) {
      throw new IllegalArgumentException("TeamId cannot be null.");
    }
    return userTeamRepository.getMembers(teamId);
  }

  @Transactional
  @Override
  public Mono<User> create(final UserUpsertModel userUpsertModel) {
    log.info("Creating user with id: {}", userUpsertModel.id());
    var userToSave = userUpsertModel.mergeWith(User.builder().build(), true);
    userValidator.validateForSave(userToSave);
    return userRepository
        .upsert(userToSave)
        .flatMap(id -> userRepository.findById(id, false))
        .flatMap(user -> userAuthManager.register(user).thenReturn(user))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "User with login" + userUpsertModel.email() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<User> update(final UserUpsertModel userUpsertModel) {
    log.info("Updating user with id: {}", userUpsertModel.id());
    return userRepository
        .findById(userUpsertModel.id(), false)
        .singleOptional()
        // stub User for saving new entity with empty id
        .map(userOpt -> userOpt.orElse(User.builder().build()))
        .map(userUpsertModel::mergeWith)
        .doOnNext(userValidator::validateForSave)
        .flatMap(userRepository::upsert)
        .flatMap(id -> userRepository.findById(id, false))
        .flatMap(user -> userAuthManager.onUserChanged(user).thenReturn(user))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "User with login"
                            + userUpsertModel.email()
                            + " already exists. Cannot update user."));
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

    return userRepository
        .delete(id)
        .flatMap(entitiesDeleted -> userAuthManager.onUserDeleted(id).thenReturn(entitiesDeleted));
  }

  /**
   * Finds all users by filters (non-admin users).
   *
   * @param filterOptions filter options to filter users
   * @param pageable page properties such as page number, page size, sort order, etc.
   * @return
   */
  @Transactional(readOnly = true)
  @Override
  public Mono<Page<User>> findAll(final UserFilterOptions filterOptions, final Pageable pageable) {
    if (pageable == null) {
      throw new IllegalArgumentException(
          "Page parameters are mandatory. Please provide Pageable object with specified page params.");
    }
    var emptyOptions = new UserFilterOptions(null, null, null, null, null);
    return userRepository.findAll(filterOptions == null ? emptyOptions : filterOptions, pageable);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<BigDecimal> getUserBalance(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.getUserBalance(id);
  }

  @Transactional
  @Override
  public Mono<SetUserTeamMembershipResponse> setUserTeamMemberships(
      UUID teamId, ImmutableSet<UUID> userIds) {
    if (teamId == null || userIds == null) {
      throw new BusinessLogicExpectationFailedException(
          "GroupId and user ids cannot be null to update/create membership!");
    }
    return userRepository
        .findAllById(userIds)
        .collectList()
        .flatMap(
            foundUsers ->
                getValidationErrorsMono(
                        teamId, userIds, foundUsers, ImmutableSet.copyOf(foundUsers))
                    .flatMap(
                        validationResult -> {
                          if (!validationResult.success()) {
                            return Mono.just(validationResult);
                          }
                          // if request is valid
                          return userTeamRepository
                              .setUserTeamMemberships(
                                  teamId,
                                  foundUsers.stream()
                                      .map(User::id)
                                      .collect(ImmutableSet.toImmutableSet()))
                              .collectList()
                              .thenReturn(validationResult);
                        }));
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<SetUserTeamMembershipResponse> validateUserTeamMemberships(
      UUID teamId, ImmutableSet<UUID> userIds) {
    if (teamId == null || userIds == null) {
      throw new BusinessLogicExpectationFailedException(
          "GroupId and user ids cannot be null to update/create membership!");
    }
    return userRepository
        .findAllById(userIds)
        .collectList()
        .flatMap(
            foundUsers ->
                getValidationErrorsMono(
                    teamId, userIds, foundUsers, ImmutableSet.copyOf(foundUsers)));
  }

  private Mono<SetUserTeamMembershipResponse> getValidationErrorsMono(
      UUID teamId, ImmutableSet<UUID> userIds, List<User> foundUsers, ImmutableSet<User> dbUsers) {
    if (dbUsers.size() != userIds.size()) {
      return Mono.just(
          new ValidationErrors(
              Optional.of(
                  Sets.difference(
                          userIds,
                          dbUsers.stream().map(User::id).collect(ImmutableSet.toImmutableSet()))
                      .immutableCopy()),
              Optional.empty()));
    }

    // return notFoundIds of admin users, because they are not allowed to be in groups^ and cannot
    // be retrieved by users
    if (dbUsers.stream().anyMatch(u -> UserRole.ADMIN.equals(u.role()))) {
      log.warn("Request contains admin users in group update.");
      return Mono.just(
          new ValidationErrors(
              Optional.of(
                  dbUsers.stream()
                      .filter(u -> UserRole.ADMIN.equals(u.role()))
                      .map(User::id)
                      .collect(ImmutableSet.toImmutableSet())),
              Optional.empty()));
    }

    var occupiedStudentsGroupsF =
        userTeamManager.getUserGroups(
            foundUsers.stream()
                .filter(u -> UserRole.LEARNER.equals(u.role()))
                .collect(ImmutableList.toImmutableList()));

    return occupiedStudentsGroupsF.map(
        occupiedStudentsGroups -> {
          if (occupiedStudentsGroups.values().stream()
              .flatMap(List::stream)
              .anyMatch(t -> !t.id().equals(teamId))) {
            // convert to validation errors
            return new ValidationErrors(
                Optional.empty(),
                Optional.of(
                    occupiedStudentsGroups.entrySet().stream()
                        .collect(
                            ImmutableMap.toImmutableMap(
                                Map.Entry::getKey,
                                entry -> ImmutableList.copyOf(entry.getValue())))));
          }

          return new Success();
        });
  }
}
