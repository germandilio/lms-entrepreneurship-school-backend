package ru.hse.lmsteam.backend.repository.impl;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.repository.UserGroupRepository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserGroupRepositoryImpl implements UserGroupRepository {
  private final MasterSlaveDbOperations db;

  @Override
  public Flux<User> getMembers(Integer groupId) {
    if (groupId == null) {
      throw new IllegalArgumentException("GroupId is null!");
    }

    return db.slave
        .getDatabaseClient()
        .sql(
            "SELECT users.* FROM users_groups RIGHT JOIN users ON users_groups.user_id = users.id WHERE users_groups.group_id = :groupId")
        .bind("groupId", groupId)
        .mapProperties(User.class)
        .all();
  }

  @Override
  public Flux<Group> getUserGroups(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("UserId is null!");
    }

    return db.slave
        .getDatabaseClient()
        .sql(
            "SELECT groups.* FROM users_groups RIGHT JOIN groups ON users_groups.group_id = groups.id WHERE users_groups.user_id = :userId")
        .bind("userId", userId)
        .mapProperties(Group.class)
        .all();
  }

  @Override
  public Flux<UUID> setUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds) {
    if (groupId == null) {
      throw new IllegalArgumentException("GroupId is null!");
    }
    if (userIds == null) {
      throw new IllegalArgumentException("UserIds is null!");
    }

    var userIdsClause = userIds.stream().map(UUID::toString).reduce((a, b) -> a + ", " + b);

    return db.master
        .getDatabaseClient()
        .sql(
            "DELETE FROM users_groups WHERE group_id = :groupId AND user_id "
                + (userIdsClause.map(s -> "NOT IN (" + s + ")").orElse("IS NOT NULL")))
        .bind("groupId", groupId)
        .fetch()
        .rowsUpdated()
        .thenMany(batchInsertUserGroupMemberships(groupId, userIds));
  }

  private Flux<UUID> batchInsertUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds) {
    var userIdsClause = userIds.stream().map(UUID::toString).reduce((a, b) -> a + ", " + b);

    return db.master
        .getDatabaseClient()
        .sql(
            "INSERT INTO users_groups (group_id, user_id) SELECT :groupId, id FROM users WHERE id IN ("
                + userIdsClause.get()
                + ")")
        .bind("groupId", groupId)
        .fetch()
        .rowsUpdated()
        .thenMany(Flux.fromIterable(userIds));
  }
}
