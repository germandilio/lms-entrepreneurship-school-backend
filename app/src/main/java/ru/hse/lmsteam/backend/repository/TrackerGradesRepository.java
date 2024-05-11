package ru.hse.lmsteam.backend.repository;

import java.util.Collection;
import java.util.UUID;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.domain.grades.TrackerGrade;

public interface TrackerGradesRepository {
  Flux<TrackerGrade> findByGradeId(UUID gradeId);

  Flux<TrackerGrade> findByGradeId(UUID gradeId, boolean operateOnMaster);

  Flux<TrackerGrade> findByGradeIds(Collection<UUID> gradeId);

  Flux<TrackerGrade> findByTrackerId(UUID trackerId);
}
