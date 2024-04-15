package ru.hse.lmsteam.backend.service.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.repository.UserTeamRepository;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.Success;
import ru.hse.lmsteam.backend.service.model.teams.ValidationErrors;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;
import ru.hse.lmsteam.backend.service.model.user.UserSnippet;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.backend.service.validation.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
  private final UserRepository userRepository;
  private final UserTeamRepository userTeamRepository;
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

  @Override
  public Flux<User> findTeamMembers(UUID teamId) {
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
                    new ValidationException(
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
        .findById(userUpsertModel.id(), true)
        .singleOptional()
        // stub User for saving new entity with empty id
        .map(userOpt -> userOpt.orElse(User.builder().build()))
        .map(userUpsertModel::mergeWith)
        .doOnNext(userValidator::validateForSave)
        .flatMap(userRepository::upsert)
        .flatMap(id -> userRepository.findById(id, false))
        .flatMap(user -> userAuthManager.onUserChanged(user).thenReturn(user));
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
  public Flux<UserSnippet> getUserSnippets() {
    return userRepository.allUserSnippets();
  }

  @Override
  public Mono<BigDecimal> getUserBalance(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id cannot be null to get balance!");
    }
    return userRepository.getUserBalance(id);
  }

  @Transactional
  @Override
  public Mono<SetUserTeamMembershipResponse> setUserTeamMemberships(
      UUID teamId, ImmutableSet<UUID> userIds) {
    if (teamId == null || userIds == null) {
      throw new IllegalArgumentException(
          "GroupId and user ids cannot be null to update/create membership!");
    }
    return userRepository
        .findAllById(userIds)
        .collectList()
        .flatMap(
            foundUsers -> {
              var dbUserIds =
                  foundUsers.stream().map(User::id).collect(ImmutableSet.toImmutableSet());
              Mono<ValidationErrors> validationErrors =
                  getValidationErrorsMono(userIds, foundUsers, dbUserIds);
              if (validationErrors != null) return validationErrors;

              return userTeamRepository
                  .setUserTeamMemberships(teamId, dbUserIds)
                  .collectList()
                  .thenReturn(new Success());
            });
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<SetUserTeamMembershipResponse> validateUserTeamMemberships(
      UUID teamId, ImmutableSet<UUID> userIds) {
    if (teamId == null || userIds == null) {
      throw new IllegalArgumentException(
          "GroupId and user ids cannot be null to update/create membership!");
    }
    return userRepository
        .findAllById(userIds)
        .collectList()
        .flatMap(
            foundUsers -> {
              var dbUserIds =
                  foundUsers.stream().map(User::id).collect(ImmutableSet.toImmutableSet());
              Mono<ValidationErrors> validationErrors =
                  getValidationErrorsMono(userIds, foundUsers, dbUserIds);
              if (validationErrors != null) return validationErrors;

              return Mono.just(new Success());
            });
  }

  private Mono<ValidationErrors> getValidationErrorsMono(
      ImmutableSet<UUID> userIds, List<User> foundUsers, ImmutableSet<UUID> dbUserIds) {
    if (dbUserIds.size() != userIds.size()) {
      return Mono.just(
          new ValidationErrors(
              Optional.of(Sets.difference(userIds, dbUserIds).immutableCopy()), Optional.empty()));
    }

    // collect map <user, list<group>> for only users already in groups
    var occupiedStudentsGroups =
        foundUsers.stream()
            .map(
                user -> {
                  try {
                    var groups =
                        Optional.ofNullable(
                                userTeamRepository
                                    .getUserTeams(user.id())
                                    .collectList()
                                    .toFuture()
                                    .get())
                            .orElse(List.of());
                    return Map.entry(user, groups);
                  } catch (Exception e) {
                    throw new RuntimeException(e);
                  }
                })
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    if (!occupiedStudentsGroups.isEmpty()) {
      // convert to validation errors
      return Mono.just(
          new ValidationErrors(
              Optional.empty(),
              Optional.of(
                  occupiedStudentsGroups.entrySet().stream()
                      .collect(
                          ImmutableMap.toImmutableMap(
                              entry -> entry.getKey(),
                              entry -> ImmutableList.copyOf(entry.getValue()))))));
    }
    return null;
  }
}
