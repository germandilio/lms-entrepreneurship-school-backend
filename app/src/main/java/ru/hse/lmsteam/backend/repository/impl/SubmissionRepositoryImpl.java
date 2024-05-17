package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.Parameters;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.submission.SubmissionDB;
import ru.hse.lmsteam.backend.repository.SubmissionRepository;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;

@Repository
@RequiredArgsConstructor
public class SubmissionRepositoryImpl implements SubmissionRepository {
  private final MasterSlaveDbOperations db;
  private final PlainSQLQueryTranslator<SubmissionFilterOptions> submissionFilterOptionsQT;

  @Override
  public Mono<SubmissionDB> findById(UUID id) {
    return findById(id, false);
  }

  @Override
  public Mono<SubmissionDB> findById(UUID id, boolean operateOnMaster) {
    if (id == null) {
      throw new IllegalArgumentException("Submission id is null!");
    }

    var operations = operateOnMaster ? this.db.master : this.db.slave;
    return operations.selectOne(query(where("id").is(id)), SubmissionDB.class);
  }

  @Override
  public Mono<SubmissionDB> findByTaskAndOwner(UUID taskId, UUID ownerId) {
    if (taskId == null) {
      throw new IllegalArgumentException("Task id is null!");
    }

    if (ownerId == null) {
      throw new IllegalArgumentException("Owner id is null!");
    }

    return this.db.slave.selectOne(
        query(where("task_id").is(taskId).and("owner_id").is(ownerId)), SubmissionDB.class);
  }

  @Override
  public Flux<SubmissionDB> findAllByIds(Collection<UUID> ids) {
    if (ids == null) {
      throw new IllegalArgumentException("Ids is null!");
    }

    return this.db.slave.select(query(where("task_id").in(ids)), SubmissionDB.class);
  }

  @Override
  public Flux<SubmissionDB> findAllByTaskIds(Collection<UUID> taskIds) {
    if (taskIds == null) {
      throw new IllegalArgumentException("Task id is null!");
    }

    return this.db.slave.select(query(where("task_id").in(taskIds)), SubmissionDB.class);
  }

  @Override
  public Mono<Page<SubmissionDB>> findAll(
      SubmissionFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null) {
      throw new IllegalArgumentException("FilterOptions are null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }

    return this.db
        .slave
        .getDatabaseClient()
        .sql(submissionFilterOptionsQT.translateToSql(filterOptions, pageable))
        .mapProperties(SubmissionDB.class)
        .all()
        .collectList()
        .zipWith(
            db.slave
                .getDatabaseClient()
                .sql(submissionFilterOptionsQT.translateToCountSql(filterOptions))
                .mapValue(Long.class)
                .one())
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }

  @Override
  public Mono<SubmissionDB> upsert(SubmissionDB submission) {
    if (submission == null) {
      throw new IllegalArgumentException("Submission is null!");
    }

    if (submission.id() == null) {
      return this.db.master.insert(submission);
    } else {
      return this.db.master.update(submission);
    }
  }

  @Override
  public Flux<SubmissionDB> batchUpsert(Collection<SubmissionDB> submissions) {
    if (submissions == null) {
      throw new IllegalArgumentException("Submissions are null!");
    }

    // generate ids for new submissions in code, because it is not possible to generate them in SQL
    // while bulk upsert.
    // Query performance is more important here.
    var preparedSubmissions =
        submissions.stream().map(s -> s.id() == null ? s.withId(UUID.randomUUID()) : s).toList();

    return db.master
        .getDatabaseClient()
        .sql(buildBatchUpsertQuery(preparedSubmissions))
        .bindValues(buildParameters(preparedSubmissions))
        .mapProperties(SubmissionDB.class)
        .all();
  }

  @Override
  public Mono<Long> deleteAllGroupSubmissions(UUID taskId, UUID groupId) {
    if (taskId == null) {
      throw new IllegalArgumentException("Task id is null!");
    }

    if (groupId == null) {
      throw new IllegalArgumentException("Group id is null!");
    }

    return this.db.master.delete(
        query(where("task_id").is(taskId).and("team_id").is(groupId)), SubmissionDB.class);
  }

  @Override
  public Mono<Long> deleteAllByTaskIds(Collection<UUID> taskIds) {
    if (taskIds == null) {
      throw new IllegalArgumentException("Task id is null!");
    }

    return this.db.master.delete(query(where("task_id").in(taskIds)), SubmissionDB.class);
  }

  @Override
  public Flux<SubmissionDB> findAll() {
    return db.slave.select(Query.query(Criteria.empty()), SubmissionDB.class);
  }

  private String buildBatchUpsertQuery(Collection<SubmissionDB> submissions) {
    var SqlTemplateNames =
        "(id, task_id, owner_id, publisher_id, team_id, submission_date, payload)";
    var doSetUpdateClause =
        "task_id = excluded.task_id, owner_id = excluded.owner_id, publisher_id = excluded.publisher_id, team_id = excluded.team_id, submission_date = excluded.submission_date, payload = excluded.payload";

    return "INSERT INTO submissions "
        + SqlTemplateNames
        + " VALUES "
        + getSqlTemplates(submissions.size())
        + " ON CONFLICT (id) DO UPDATE SET "
        + doSetUpdateClause
        + " RETURNING submissions.*";
  }

  private String getSqlTemplates(int valuesCount) {
    if (valuesCount <= 0) {
      throw new IllegalArgumentException("Values count is less than 1!");
    }

    var stringBuilder = new StringBuilder(80 * valuesCount);
    for (int index = 0; index < valuesCount; index++) {
      stringBuilder.append("(");
      stringBuilder.append(":submission").append(index).append(", ");
      stringBuilder.append(":task").append(index).append(", ");
      stringBuilder.append(":owner").append(index).append(", ");
      stringBuilder.append(":publisher").append(index).append(", ");
      stringBuilder.append(":team").append(index).append(", ");
      stringBuilder.append(":submissionDate").append(index).append(", ");
      stringBuilder.append(":payload").append(index).append(")");

      if (index < valuesCount - 1) {
        stringBuilder.append(", ");
      }
    }
    return stringBuilder.toString();
  }

  private Map<String, Parameter> buildParameters(Collection<SubmissionDB> submissions) {
    if (submissions == null) {
      throw new IllegalArgumentException("Submissions are null!");
    }

    var parameters = new HashMap<String, Parameter>(submissions.size() * 9);
    var index = 0;
    for (var submission : submissions) {
      parameters.put("submission" + index, Parameters.in(submission.id()));
      parameters.put("task" + index, Parameters.in(submission.taskId()));
      parameters.put("owner" + index, Parameters.in(submission.ownerId()));
      parameters.put("publisher" + index, Parameters.in(submission.publisherId()));
      if (submission.teamId() == null) {
        parameters.put("team" + index, Parameters.in(UUID.class));
      } else {
        parameters.put("team" + index, Parameters.in(submission.teamId()));
      }
      parameters.put("submissionDate" + index, Parameters.in(submission.submissionDate()));
      parameters.put("payload" + index, Parameters.in(submission.payload()));
      index++;
    }
    return parameters;
  }
}
