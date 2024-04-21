package ru.hse.lmsteam.backend.repository.impl;

import static java.util.stream.Collectors.joining;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.repository.TeamRepository;
import ru.hse.lmsteam.backend.repository.query.translators.QueryTranslator;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;

@Repository
public class TeamRepositoryImpl implements TeamRepository {
  private final MasterSlaveDbOperations db;
  private final QueryTranslator<TeamsFilterOptions> teamsFilterOptionsQTranslator;

  public TeamRepositoryImpl(
      MasterSlaveDbOperations db,
      @Qualifier("teamFilterOptionsQT") QueryTranslator<TeamsFilterOptions> teamsFilterOptionsQTranslator) {
    this.db = db;
    this.teamsFilterOptionsQTranslator = teamsFilterOptionsQTranslator;
  }

  @Override
  public Mono<Team> findById(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    return db.slave.selectOne(query(where("id").is(id).and("is_deleted").isFalse()), Team.class);
  }

  @Override
  public Mono<Team> findById(UUID id, boolean forUpdate) {
    if (id == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    var sql =
        "SELECT * FROM teams WHERE id = :id AND is_deleted = false "
            + (forUpdate ? " FOR UPDATE" : "");

    return db.master.getDatabaseClient().sql(sql).bind("id", id).mapProperties(Team.class).one();
  }

  @Override
  public Flux<Team> findByIds(ImmutableSet<UUID> ids, boolean forUpdate) {
    if (ids == null) {
      throw new IllegalArgumentException("Id is null!");
    }
    var idsClause = ids.stream().map(String::valueOf).collect(joining(", ", "(", ")"));
    var sql =
        "SELECT * FROM teams WHERE is_deleted = false AND id IN "
            + idsClause
            + (forUpdate ? " FOR UPDATE" : "");

    return db.master.getDatabaseClient().sql(sql).mapProperties(Team.class).all();
  }

  @Override
  public Mono<Page<Team>> findAll(TeamsFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null) {
      throw new IllegalArgumentException("FilterOptions is null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }

    var preparedSQLSelect = teamsFilterOptionsQTranslator.translate(filterOptions).with(pageable);
    return db.slave
        .select(preparedSQLSelect, Team.class)
        .collectList()
        .zipWith(db.slave.count(preparedSQLSelect, Team.class))
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }

  @Override
  public Mono<UUID> upsert(Team team) {
    if (team == null) {
      throw new IllegalArgumentException("Group is null!");
    }

    if (team.id() == null) {
      return db.master.insert(team).map(Team::id);
    } else {
      return db.master.update(team).map(Team::id);
    }
  }

  @Override
  public Mono<Long> delete(UUID groupId) {
    return db.master
        .getDatabaseClient()
        .sql("UPDATE teams SET is_deleted = true WHERE id = :id")
        .bind("id", groupId)
        .fetch()
        .rowsUpdated();
  }
}
