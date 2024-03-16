package ru.hse.lmsteam.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.service.model.GroupsFilterOptions;

public interface GroupRepository {
  Mono<Group> findById(Integer id);

  /**
   * Find group by id. If forUpdate = true - locks the entity in database. Operates on master
   *
   * @param id group id
   * @param forUpdate whether to acquire row lock in database.
   * @return group entity
   */
  Mono<Group> findById(Integer id, boolean forUpdate);

  Mono<Page<Group>> findAll(GroupsFilterOptions filterOptions, Pageable pageable);

  Mono<Integer> upsert(Group group);

  Mono<Long> delete(Integer group);
}
