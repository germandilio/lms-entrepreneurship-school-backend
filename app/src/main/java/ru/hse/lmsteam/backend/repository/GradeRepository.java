package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.GradeDB;
import ru.hse.lmsteam.backend.domain.grades.TaskType;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

public interface GradeRepository {
  Mono<GradeDB> findById(UUID id);

  Mono<GradeDB> findById(UUID id, boolean operateOnMaster);

  Mono<GradeDB> findBySubmissionId(UUID submissionId);

  Mono<GradeDB> upsert(GradeDB gradeDB);

  Mono<Page<GradeDB>> findAll(GradesFilterOptions filterOptions, Pageable pageable);

  Flux<GradeDB> findByTaskType(TaskType taskType);

  Flux<GradeDB> getAll();
}
