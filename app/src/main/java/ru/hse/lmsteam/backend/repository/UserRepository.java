package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

public interface UserRepository {
  Mono<User> findById(UUID id);

  /**
   * Locks entity with provided id in database.
   * @param id user uuid
   * @return retrieved entity
   */
  Mono<User> findByIdForUpdate(UUID id);

  Mono<User> saveOne(User user);

  Mono<Long> deleteById(UUID id);

  Flux<User> findAll(UserFilterOptions filterOptions, Pageable pageable);

  Flux<String> allUserNames();
}
