package ru.hse.lmsteam.backend.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;
import ru.hse.lmsteam.backend.service.validation.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagerImpl implements UserManager {
  private final UserRepository userRepository;
  private final UserValidator userValidator;

  @Override
  public Mono<User> getUser(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.findById(id);
  }

  @Override
  public Mono<User> updateOrCreateUser(final User user) {
    userValidator.validateForSave(user);
    return userRepository.saveOne(user);
  }

  @Override
  public Mono<Long> deleteUser(final UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return userRepository.deleteById(id);
  }

  @Override
  public Flux<User> findUsers(final UserFilterOptions filterOptions, final Pageable pageable) {
    if (pageable == null) {
      throw new IllegalArgumentException(
          "Page parameters are mandatory. Please provide Pageable object with specified page params.");
    }
    var emptyOptions = new UserFilterOptions(null, null, null, null, null);
    return userRepository.findAll(filterOptions == null ? emptyOptions : filterOptions, pageable);
  }

  @Override
  public Flux<String> getUserNamesList() {
    return userRepository.allUserNames();
  }
}
