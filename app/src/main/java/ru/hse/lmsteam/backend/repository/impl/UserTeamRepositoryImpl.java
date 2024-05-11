package ru.hse.lmsteam.backend.repository.impl;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.repository.UserTeamRepository;
import ru.hse.lmsteam.backend.service.model.teams.UserTeam;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserTeamRepositoryImpl implements UserTeamRepository {
  private final MasterSlaveDbOperations db;

  @Override
  public Flux<User> getMembers(UUID teamId) {
    if (teamId == null) {
      throw new IllegalArgumentException("TeamId is null!");
    }

    return db.slave
        .getDatabaseClient()
        .sql(
            "SELECT users.* FROM users_teams RIGHT JOIN users ON users_teams.user_id = users.id WHERE users_teams.team_id = :teamId AND users.is_deleted = false")
        .bind("teamId", teamId)
        .mapProperties(User.class)
        .all();
  }

  @Override
  public Flux<Team> getUserTeams(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("UserId is null!");
    }

    return db.slave
        .getDatabaseClient()
        .sql(
            "SELECT teams.* FROM users_teams RIGHT JOIN teams ON users_teams.team_id = teams.id WHERE users_teams.user_id = :userId AND teams.is_deleted = false")
        .bind("userId", userId)
        .mapProperties(Team.class)
        .all();
  }

  @Override
  public Flux<UserTeam> getUsersTeams(Collection<UUID> userId) {
    if (userId == null) {
      throw new IllegalArgumentException("UserId is null!");
    }

    var userIdsClause = userId.stream().map(id -> "'" + id + "'").reduce((a, b) -> a + ", " + b);
    var sqlIdsClause = userIdsClause.map(s -> " users_teams.user_id IN (" + s + ")").orElse("1=1");

    return db.slave
        .getDatabaseClient()
        .sql(
            "SELECT users_teams.user_id, teams.* FROM users_teams LEFT JOIN teams ON users_teams.team_id = teams.id WHERE "
                + sqlIdsClause
                + " AND teams.is_deleted = false")
        .map(
            (row, rowMetadata) -> {
              var team =
                  Team.builder()
                      .id(row.get("id", UUID.class))
                      .number(row.get("number", Integer.class))
                      .projectTheme(row.get("projectTheme", String.class))
                      .description(row.get("description", String.class))
                      .isDeleted(row.get("isDeleted", Boolean.class))
                      .build();
              return new UserTeam(row.get("user_id", UUID.class), team);
            })
        .all();
  }

  @Override
  public Flux<User> getTeammates(UUID memberId) {
    if (memberId == null) {
      throw new IllegalArgumentException("MemberId is null!");
    }

    return db.slave
        .getDatabaseClient()
        .sql(
            "SELECT DISTINCT ON (users.id) users.* FROM users_teams LEFT JOIN users ON users.id = users_teams.user_id WHERE team_id IN (SELECT team_id FROM users_teams WHERE user_id = :memberId)")
        .bind("memberId", memberId)
        .mapProperties(User.class)
        .all();
  }

  @Override
  public Flux<UUID> setUserTeamMemberships(UUID teamId, ImmutableSet<UUID> userIds) {
    if (teamId == null) {
      throw new IllegalArgumentException("GroupId is null!");
    }
    if (userIds == null) {
      throw new IllegalArgumentException("UserIds is null!");
    }

    return db.master
        .getDatabaseClient()
        .sql("DELETE FROM users_teams WHERE team_id = :teamId")
        .bind("teamId", teamId)
        .fetch()
        .rowsUpdated()
        .thenMany(batchInsertUserTeamMemberships(teamId, userIds));
  }

  private Flux<UUID> batchInsertUserTeamMemberships(UUID teamId, ImmutableSet<UUID> userIds) {
    var userIdsClause =
        userIds.stream().map(id -> "'" + id.toString() + "'").reduce((a, b) -> a + ", " + b);

    var sqlIdsClause = userIdsClause.map(s -> " id IN (" + s + ")").orElse("1=1");
    return db.master
        .getDatabaseClient()
        .sql(
            "INSERT INTO users_teams (team_id, user_id) SELECT :teamId, id FROM users WHERE "
                + sqlIdsClause)
        .bind("teamId", teamId)
        .fetch()
        .rowsUpdated()
        .thenMany(Flux.fromIterable(userIds));
  }
}
