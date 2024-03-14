package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;

public interface UserManager {
  Mono<User> findById(UUID id);

  Mono<User> update(UserUpsertModel user);

  Mono<User> create(UserUpsertModel user);

  Mono<Long> delete(UUID id);

  /**
   * Finds users by filter options and page properties.
   *
   * @param filterOptions filter options to filter users
   * @param pageable page properties such as page number, page size, sort order, etc.
   * @return users collection
   */
  Mono<Page<User>> findAll(UserFilterOptions filterOptions, Pageable pageable);

  /**
   * Return list of usernames in the most efficient way.
   *
   * @return list of usernames
   */
  Flux<String> getUserNamesList();

  /**
   * Set user group memberships.
   *
   * @param groupId group which user will be added to
   * @param userIds list of users to be added to the group
   * @return list of updated user entities (members ofr the group)
   */
  Flux<User> setUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds);
}
