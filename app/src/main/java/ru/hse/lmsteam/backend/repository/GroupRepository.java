package ru.hse.lmsteam.backend.repository;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.Group;
import ru.hse.lmsteam.backend.service.model.GroupsFilterOptions;

public interface GroupRepository {
  Mono<Group> findById(Integer id);

  /**
   * Find group by id. If forUpdate = true - locks the entity in database.
   * Operates on master
   * @param id group id
   * @param forUpdate whether to acquire row lock in database.
   * @return group entity
   */
  Mono<Group> findById(Integer id, boolean forUpdate);

  Flux<Group> findAll(GroupsFilterOptions filterOptions, Pageable pageable);

  Mono<Integer> upsert(Group group);

  Mono<Long> delete(Integer group);
}
