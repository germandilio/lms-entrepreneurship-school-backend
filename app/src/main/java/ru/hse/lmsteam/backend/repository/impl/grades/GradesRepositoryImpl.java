package ru.hse.lmsteam.backend.repository.impl.grades;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
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
import ru.hse.lmsteam.backend.domain.grades.GradeDB;
import ru.hse.lmsteam.backend.domain.grades.TaskType;
import ru.hse.lmsteam.backend.repository.GradeRepository;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

@Repository
@RequiredArgsConstructor
public class GradesRepositoryImpl implements GradeRepository {
  private final MasterSlaveDbOperations db;
  private final PlainSQLQueryTranslator<GradesFilterOptions> gradesFilterOptionsQT;

  @Override
  public Mono<GradeDB> findById(UUID id) {
    return findById(id, false);
  }

  @Override
  public Mono<GradeDB> findById(UUID id, boolean operateOnMaster) {
    if (id == null) {
      throw new IllegalArgumentException("id is null");
    }

    var database = operateOnMaster ? db.master : db.slave;
    return database.selectOne(query(where("id").is(id)), GradeDB.class);
  }

  @Override
  public Mono<GradeDB> findBySubmissionId(UUID submissionId) {
    if (submissionId == null) {
      throw new IllegalArgumentException("submissionId is null");
    }

    return db.slave.selectOne(query(where("submission_id").is(submissionId)), GradeDB.class);
  }

  @Override
  public Mono<GradeDB> upsert(GradeDB gradeDB) {
    if (gradeDB == null) {
      throw new IllegalArgumentException("gradeDB is null");
    }

    if (gradeDB.id() == null) {
      return db.master.insert(gradeDB);
    } else {
      return db.master.update(gradeDB);
    }
  }

  @Override
  public Mono<Page<GradeDB>> findAll(GradesFilterOptions filterOptions, Pageable pageable) {
    return db.slave
        .getDatabaseClient()
        .sql(gradesFilterOptionsQT.translateToSql(filterOptions, pageable))
        .mapProperties(GradeDB.class)
        .all()
        .collectList()
        .zipWith(
            db.slave
                .getDatabaseClient()
                .sql(gradesFilterOptionsQT.translateToCountSql(filterOptions))
                .mapValue(Long.class)
                .one())
        .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
  }

  @Override
  public Flux<GradeDB> findByTaskType(TaskType taskType) {
    return db.slave.select(query(where("task_type").is(taskType)), GradeDB.class);
  }

  @Override
  public Flux<GradeDB> getAll() {
    return db.slave.select(Query.query(Criteria.empty()), GradeDB.class);
  }
}
