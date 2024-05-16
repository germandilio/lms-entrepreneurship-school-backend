package ru.hse.lmsteam.backend.repository;

import java.util.Collection;
import java.util.UUID;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.TrackerGradeDb;

public interface TrackerGradesRepository {
  Flux<TrackerGradeDb> findByGradeId(UUID gradeId);

  Flux<TrackerGradeDb> findByGradeId(UUID gradeId, boolean operateOnMaster);

  Flux<TrackerGradeDb> findByGradeIds(Collection<UUID> gradeIds);

  Flux<TrackerGradeDb> findByTrackerId(UUID trackerId);

  Mono<TrackerGradeDb> upsert(TrackerGradeDb trackerGradeDb);
}
