package ru.hse.lmsteam.backend.repository.impl.grades;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.grades.TrackerGrade;
import ru.hse.lmsteam.backend.repository.TrackerGradesRepository;

@Repository
@RequiredArgsConstructor
public class TrackerGradesRepositoryImpl implements TrackerGradesRepository {
  private final MasterSlaveDbOperations db;

  @Override
  public Flux<TrackerGrade> findByGradeId(UUID gradeId) {
    return findByGradeId(gradeId, false);
  }

  @Override
  public Flux<TrackerGrade> findByGradeId(UUID gradeId, boolean operateOnMaster) {
    if (gradeId == null) {
      throw new IllegalArgumentException("gradeId is null");
    }

    var database = operateOnMaster ? db.master : db.slave;
    return database.select(query(where("grade_id").is(gradeId)), TrackerGrade.class);
  }

  @Override
  public Flux<TrackerGrade> findByGradeIds(Collection<UUID> gradeId) {
    if (gradeId == null) {
      throw new IllegalArgumentException("gradeId is null");
    }

    return db.slave.select(query(where("grade_id").in(gradeId)), TrackerGrade.class);
  }

  @Override
  public Flux<TrackerGrade> findByTrackerId(UUID trackerId) {
    if (trackerId == null) {
      throw new IllegalArgumentException("trackerId is null");
    }

    return db.slave.select(query(where("tracker_id").is(trackerId)), TrackerGrade.class);
  }
}
