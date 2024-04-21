package ru.hse.lmsteam.backend.repository;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;

public interface TeamRepository {
  Mono<Team> findById(UUID id);

  /**
   * Find group by id. If forUpdate = true - locks the entity in database. Operates on master
   *
   * @param id group id
   * @param forUpdate whether to acquire row lock in database.
   * @return group entity
   */
  Mono<Team> findById(UUID id, boolean forUpdate);

  Flux<Team> findByIds(ImmutableSet<UUID> ids, boolean forUpdate);

  Mono<Page<Team>> findAll(TeamsFilterOptions filterOptions, Pageable pageable);

  Mono<UUID> upsert(Team team);

  Mono<Long> delete(UUID teamId);
}
