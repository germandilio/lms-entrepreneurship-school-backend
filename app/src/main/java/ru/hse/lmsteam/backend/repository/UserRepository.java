package ru.hse.lmsteam.backend.repository;

import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;
import ru.hse.lmsteam.backend.service.model.user.UserSnippet;

public interface UserRepository {
  Mono<User> findById(UUID id);

  Flux<User> findByIds(Collection<UUID> ids);

  Mono<BigDecimal> getUserBalance(UUID id);

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

  Flux<UserSnippet> allUserSnippets();
}
