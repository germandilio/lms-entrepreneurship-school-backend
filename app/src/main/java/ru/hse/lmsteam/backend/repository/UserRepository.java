package ru.hse.lmsteam.backend.repository;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

public interface UserRepository {
  Mono<User> findById(UUID id);

  /**
   * Retrieves entity operating on master db. If forUpdate = true, locks entity in db using sql 'FOR
   * UPDATE'
   *
   * @param id user uuid
   * @return retrieved entity
   */
  Mono<User> findById(UUID id, boolean forUpdate);

  Flux<User> findAllById(ImmutableSet<UUID> ids);

  Mono<UUID> upsert(User user);

  Mono<Long> delete(UUID id);

  Mono<Page<User>> findAll(UserFilterOptions filterOptions, Pageable pageable);

  Flux<String> allUserNames();

  Flux<UUID> setUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds);
}
