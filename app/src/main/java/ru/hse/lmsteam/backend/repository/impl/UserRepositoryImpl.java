package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.repository.query.translators.SimpleQueryTranslator;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {
  private final MasterSlaveDbOperations db;
  private final SimpleQueryTranslator<UserFilterOptions> userFiltersQTranslator;

  public UserRepositoryImpl(
      MasterSlaveDbOperations db,
      @Qualifier("userFilterOptionsQT") SimpleQueryTranslator<UserFilterOptions> userFiltersQTranslator) {
    this.db = db;
    this.userFiltersQTranslator = userFiltersQTranslator;
  }

  @Override
  public Mono<User> findById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id)), User.class);
  }

  @Override
  public Mono<User> findById(UUID id, boolean forUpdate) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }

    var sql = "SELECT * FROM users WHERE id = :id" + (forUpdate ? " FOR UPDATE" : "");
    return db.master.getDatabaseClient().sql(sql).bind("id", id).mapProperties(User.class).one();
  }

  @Override
  public Flux<User> findAllById(ImmutableSet<UUID> ids) {
    if (ids == null) {
      throw new IllegalArgumentException("Ids is null!");
    }
    return db.slave.select(query(where("id").in(ids)), User.class);
  }

  @Override
  public Mono<UUID> upsert(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User is null!");
    }

    if (user.id() == null) {
      return db.master.insert(user).map(User::id);
    } else {
      return db.master.update(user).map(User::id);
    }
  }

  @Override
  public Mono<Long> delete(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.master
        .getDatabaseClient()
        .sql("UPDATE users SET is_deleted = true WHERE id = :id")
        .bind("id", id)
        .fetch()
        .rowsUpdated();
  }

  @Override
  public Mono<Page<User>> findAll(UserFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null) {
      throw new IllegalArgumentException("Filter options is null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }
    return db.slave
        .getDatabaseClient()
        .sql(userFiltersQTranslator.translateToSql(filterOptions, pageable))
        .mapProperties(User.class)
        .all()
        .collectList()
        .zipWith(
            db.slave
                .getDatabaseClient()
                .sql(userFiltersQTranslator.translateToCountSql(filterOptions))
                .mapValue(Long.class)
                .one())
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }

  @Override
  public Flux<String> allUserNames() {
    return db.slave
        .getDatabaseClient()
        .sql("SELECT name FROM users")
        .map(row -> row.get("name", String.class))
        .all();
  }

  @Override
  public Flux<UUID> setUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds) {
    if (groupId == null) {
      throw new IllegalArgumentException("Group id is null!");
    }
    if (userIds == null) {
      throw new IllegalArgumentException("User ids is null!");
    }

    return db.master
        .getDatabaseClient()
        .sql("UPDATE users SET group_id = null WHERE group_id = :groupId AND id NOT IN (:userIds)")
        .bind("groupId", groupId)
        .bind("userIds", userIds)
        .fetch()
        .rowsUpdated()
        .flatMap(
            count -> {
              log.info("Removed {} users from group {}", count, groupId);
              return db.master
                  .getDatabaseClient()
                  .sql("UPDATE users SET group_id = :groupId WHERE id IN (:userIds)")
                  .bind("groupId", groupId)
                  .bind("userIds", userIds)
                  .fetch()
                  .rowsUpdated();
            })
        .map(
            count -> {
              log.info("Change group for {} users to group {}", count, groupId);
              return count;
            })
        .thenMany(Flux.fromIterable(userIds));
  }
}
