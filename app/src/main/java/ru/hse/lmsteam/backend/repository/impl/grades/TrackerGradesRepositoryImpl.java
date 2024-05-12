package ru.hse.lmsteam.backend.repository.impl.grades;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import io.r2dbc.spi.Parameter;
import io.r2dbc.spi.Parameters;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.grades.TrackerGradeDb;
import ru.hse.lmsteam.backend.repository.TrackerGradesRepository;

@Repository
@RequiredArgsConstructor
public class TrackerGradesRepositoryImpl implements TrackerGradesRepository {
  private final MasterSlaveDbOperations db;

  @Override
  public Flux<TrackerGradeDb> findByGradeId(UUID gradeId) {
    return findByGradeId(gradeId, false);
  }

  @Override
  public Flux<TrackerGradeDb> findByGradeId(UUID gradeId, boolean operateOnMaster) {
    if (gradeId == null) {
      throw new IllegalArgumentException("gradeId is null");
    }

    var database = operateOnMaster ? db.master : db.slave;
    return database.select(query(where("grade_id").is(gradeId)), TrackerGradeDb.class);
  }

  @Override
  public Flux<TrackerGradeDb> findByGradeIds(Collection<UUID> gradeId) {
    if (gradeId == null) {
      throw new IllegalArgumentException("gradeId is null");
    }

    return db.slave.select(query(where("grade_id").in(gradeId)), TrackerGradeDb.class);
  }

  @Override
  public Flux<TrackerGradeDb> findByTrackerId(UUID trackerId) {
    if (trackerId == null) {
      throw new IllegalArgumentException("trackerId is null");
    }

    return db.slave.select(query(where("tracker_id").is(trackerId)), TrackerGradeDb.class);
  }

  @Override
  public Mono<TrackerGradeDb> upsert(TrackerGradeDb trackerGradeDb) {
    if (trackerGradeDb == null
        || trackerGradeDb.gradeId() == null
        || trackerGradeDb.trackerId() == null) {
      throw new IllegalArgumentException("Invalid trackerGradeDb: " + trackerGradeDb);
    }

    return db.master
        .getDatabaseClient()
        .sql(
            "INSERT INTO tracker_grades (grade_id, tracker_id, grade, comment) VALUES (:gradeId, :trackerId, :grade, :comment) ON CONFLICT (grade_id, tracker_id) DO UPDATE SET grade = :grade, comment = :comment RETURNING *")
        .bindValues(buildParams(trackerGradeDb))
        .mapProperties(TrackerGradeDb.class)
        .one();
  }

  private Map<String, Parameter> buildParams(TrackerGradeDb trackerGradeDb) {
    var params = new HashMap<String, Parameter>();
    params.put("gradeId", Parameters.in(trackerGradeDb.gradeId()));
    params.put("trackerId", Parameters.in(trackerGradeDb.trackerId()));
    if (trackerGradeDb.grade() != null) {
      params.put("grade", Parameters.in(trackerGradeDb.grade()));
    } else {
      params.put("grade", Parameters.in(Integer.class));
    }
    if (trackerGradeDb.comment() != null) {
      params.put("comment", Parameters.in(trackerGradeDb.comment()));
    } else {
      params.put("comment", Parameters.in(String.class));
    }
    return params;
  }
}
