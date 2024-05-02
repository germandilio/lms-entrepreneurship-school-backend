package ru.hse.lmsteam.backend.repository.impl;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
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
  public Mono<SubmissionDB> save(SubmissionDB submission) {
    if (submission == null) {
      throw new IllegalArgumentException("Submission is null!");
    }

    return this.db.master.insert(submission);
  }
}
