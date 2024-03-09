package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.repository.UserRepository;
import ru.hse.lmsteam.backend.repository.query.translators.UserFilterOptionsQueryTranslator;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
  private final MasterSlaveDbOperations db;

  private final UserFilterOptionsQueryTranslator userFiltersQTranslator;

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
  public Mono<UUID> saveOne(User user) {
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
  public Mono<Long> deleteById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.master.delete(query(where("id").is(id)), User.class);
  }

  @Override
  public Flux<User> findAll(UserFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null) {
      throw new IllegalArgumentException("Filter options is null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }
    return db.slave.select(
        userFiltersQTranslator.translate(filterOptions).with(pageable), User.class);
  }

  @Override
  public Flux<String> allUserNames() {
    return db.slave
        .getDatabaseClient()
        .sql("SELECT name FROM users")
        .map(row -> row.get("name", String.class))
        .all();
  }
}