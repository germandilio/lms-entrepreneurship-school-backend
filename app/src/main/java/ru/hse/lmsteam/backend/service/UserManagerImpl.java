package ru.hse.lmsteam.backend.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.User;
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
  public Mono<User> getUser(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<User> createUser(final UserUpsertModel userUpsertModel) {
    var userToSave = userUpsertModel.mergeWith(User.builder().build(), true);
    userValidator.validateForSave(userToSave);
    return userRepository.saveOne(userToSave).flatMap(id -> userRepository.findById(id, false));
  }

  @Transactional
  @Override
  public Mono<User> updateUser(final UserUpsertModel userUpsertModel) {
    return userRepository
        .findById(userUpsertModel.id(), true)
        .singleOptional()
        // stub User for saving new entity with empty id
        .map(userOpt -> userOpt.orElse(User.builder().build()))
        .map(userUpsertModel::mergeWith)
        .doOnNext(userValidator::validateForSave)
        .flatMap(userRepository::saveOne)
        .flatMap(id -> userRepository.findById(id, false));
  }

  @Transactional
  @Override
  public Mono<Long> deleteUser(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<User> findUsers(final UserFilterOptions filterOptions, final Pageable pageable) {
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
}
