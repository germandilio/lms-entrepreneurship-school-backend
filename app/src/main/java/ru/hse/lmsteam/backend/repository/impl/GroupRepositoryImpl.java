package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.user.Group;
import ru.hse.lmsteam.backend.repository.GroupRepository;
import ru.hse.lmsteam.backend.repository.query.translators.QueryTranslator;
import ru.hse.lmsteam.backend.service.model.GroupsFilterOptions;

@Repository
public class GroupRepositoryImpl implements GroupRepository {
  private final MasterSlaveDbOperations db;
  private final QueryTranslator<GroupsFilterOptions> groupsFilterOptionsQTranslator;

  public GroupRepositoryImpl(
      MasterSlaveDbOperations db,
      @Qualifier("groupFilterOptionsQT")
          QueryTranslator<GroupsFilterOptions> groupsFilterOptionsQTranslator) {
    this.db = db;
    this.groupsFilterOptionsQTranslator = groupsFilterOptionsQTranslator;
  }

  @Override
  public Mono<Group> findById(Integer id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id)), Group.class);
  }

  @Override
  public Mono<Group> findById(Integer id, boolean forUpdate) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    var sql = "SELECT * FROM groups WHERE id = :id" + (forUpdate ? " FOR UPDATE" : "");

    return db.master.getDatabaseClient().sql(sql).bind("id", id).mapProperties(Group.class).one();
  }

  @Override
  public Flux<Group> findAll(GroupsFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null) {
      throw new IllegalArgumentException("FilterOptions is null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }

    return db.slave.select(
        groupsFilterOptionsQTranslator.translate(filterOptions).with(pageable), Group.class);
  }

  @Override
  public Mono<Integer> upsert(Group group) {
    if (group == null) {
      throw new IllegalArgumentException("Group is null!");
    }

    if (group.id() == null) {
      return db.master.insert(group).map(Group::id);
    } else {
      return db.master.update(group).map(Group::id);
    }
  }

  @Override
  public Mono<Long> delete(Integer groupId) {
    return db.master
        .getDatabaseClient()
        .sql("UPDATE groups SET is_deleted = true WHERE id = :id")
        .bind("id", groupId)
        .fetch()
        .rowsUpdated();
  }
}