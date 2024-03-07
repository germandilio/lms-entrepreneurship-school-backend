package ru.hse.lmsteam.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

public interface UserManager {
  Mono<User> getUser(UUID id);

  /**
   * Updates or creates user. If user has id, then it will be updated, otherwise it will be created.
   * It is recommended to pass null as id, if you want to create new user.
   *
   * @param user user entity to update or create
   * @return updated or created user entity
   */
  Mono<User> updateOrCreateUser(User user);

  Mono<Long> deleteUser(UUID id);

  /**
   * Finds users by filter options and page properties.
   *
   * @param filterOptions filter options to filter users
   * @param pageable page properties such as page number, page size, sort order, etc.
   * @return users collection
   */
  Flux<User> findUsers(UserFilterOptions filterOptions, Pageable pageable);

  /**
   * Return list of usernames in the most efficient way.
   *
   * @return list of usernames
   */
  Flux<String> getUserNamesList();
}
