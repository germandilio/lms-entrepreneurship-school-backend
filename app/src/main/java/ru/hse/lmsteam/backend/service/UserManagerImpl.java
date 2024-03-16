package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableSet;
import jakarta.validation.ValidationException;
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
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;
import ru.hse.lmsteam.backend.service.validation.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
  private final UserRepository userRepository;
  private final UserValidator userValidator;

  @Transactional(readOnly = true)
  @Override
  public Mono<User> findById(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<User> create(final UserUpsertModel userUpsertModel) {
    var userToSave = userUpsertModel.mergeWith(User.builder().build(), true);
    userValidator.validateForSave(userToSave);
    return userRepository
        .upsert(userToSave)
        .flatMap(id -> userRepository.findById(id, false))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new ValidationException(
                        "User with id" + userUpsertModel.id() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<User> update(final UserUpsertModel userUpsertModel) {
    return userRepository
        .findById(userUpsertModel.id(), true)
        .singleOptional()
        // stub User for saving new entity with empty id
        .map(userOpt -> userOpt.orElse(User.builder().build()))
        .map(userUpsertModel::mergeWith)
        .doOnNext(userValidator::validateForSave)
        .flatMap(userRepository::upsert)
        .flatMap(id -> userRepository.findById(id, false));
  }

  @Transactional
  @Override
  public Mono<Long> delete(final UUID id) {
    if (id == null) {
      return Mono.just(0L);
    }
    return userRepository.delete(id);
  }

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
  public Flux<String> getUserNamesList() {
    return userRepository.allUserNames();
  }

  @Transactional
  @Override
  public Flux<User> setUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds) {
    if (groupId == null || userIds == null) {
      throw new IllegalArgumentException(
          "GroupId and user ids cannot be null to update/create membership!");
    }
    return userRepository
        .setUserGroupMemberships(groupId, userIds)
        .collectList()
        .thenMany(userRepository.findAllById(userIds));
  }
}
