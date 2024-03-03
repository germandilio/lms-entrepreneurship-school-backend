package ru.hse.lmsteam.backend.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.dao.UserDbActions;
import ru.hse.lmsteam.backend.model.User;

@Repository
@RequiredArgsConstructor
public class UserDbActionsImpl implements UserDbActions {
  private final MasterSlaveDbOperations db;

  @Override
  public Mono<User> findById(String id) {
    var sql = "SELECT * FROM users WHERE id = :id";
    return db.slave.getDatabaseClient().sql(sql).bind("id", id).mapProperties(User.class).one();
  }

  @Override
  public Flux<User> findAll(int limit, int offset) {
    var sql = "SELECT * FROM users LIMIT :limit OFFSET :offset";
    return db.slave
        .getDatabaseClient()
        .sql(sql)
        .bind("limit", limit)
        .bind("offset", offset)
        .mapProperties(User.class)
        .all();
  }
}
