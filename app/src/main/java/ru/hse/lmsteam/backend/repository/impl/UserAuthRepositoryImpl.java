package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;
import ru.hse.lmsteam.backend.repository.UserAuthRepository;

@Service
@RequiredArgsConstructor
public class UserAuthRepositoryImpl implements UserAuthRepository {
  private final MasterSlaveDbOperations db;

  @Override
  public Mono<UserAuth> findByLogin(String login, boolean operateOnMaster) {
    return findByLogin(login, operateOnMaster, false);
  }

  @Override
  public Mono<UserAuth> findByLogin(
      String login, boolean operateOnMaster, boolean shouldIncludeDeleted) {
    if (login == null) {
      throw new IllegalArgumentException("Login is null!");
    }
    var operations = operateOnMaster ? db.master : db.slave;
    var query =
        shouldIncludeDeleted
            ? query(where("login").is(login))
            : query(where("login").is(login).and("is_deleted").is(false));
    return operations.selectOne(query, UserAuth.class);
  }

  @Override
  public Mono<UserAuth> findByUserId(UUID userId, boolean operateOnMaster) {
    if (userId == null) {
      throw new IllegalArgumentException("UserId is null!");
    }
    var operations = operateOnMaster ? db.master : db.slave;
    return operations.selectOne(query(where("user_id").is(userId)), UserAuth.class);
  }

  @Override
  public Mono<UserAuth> findByToken(UUID token) {
    if (token == null) {
      throw new IllegalArgumentException("Token is null!");
    }
    return db.slave.selectOne(query(where("password_reset_token").is(token)), UserAuth.class);
  }

  @Override
  public Mono<UserAuth> insert(UserAuth userAuth) {
    if (userAuth == null) {
      throw new IllegalArgumentException("UserAuth is null!");
    }

    return db.master.insert(userAuth);
  }

  @Override
  public Mono<UserAuth> update(UserAuth userAuth) {
    if (userAuth == null) {
      throw new IllegalArgumentException("UserAuth is null!");
    }
    if (userAuth.id() == null) {
      throw new IllegalArgumentException("UserAuth id is null!");
    }

    return db.master.update(userAuth);
  }

  @Override
  public Mono<Long> delete(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("UserId is null!");
    }
    return db.master
        .getDatabaseClient()
        .sql("UPDATE users_auth SET is_deleted = true WHERE user_id = :userId")
        .bind("userId", userId)
        .fetch()
        .rowsUpdated();
  }
}
