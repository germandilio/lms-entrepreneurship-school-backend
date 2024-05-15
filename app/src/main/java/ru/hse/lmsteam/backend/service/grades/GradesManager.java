package ru.hse.lmsteam.backend.service.grades;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.Grade;
import ru.hse.lmsteam.backend.domain.grades.GradeDB;
import ru.hse.lmsteam.backend.domain.grades.TaskType;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

public interface GradesManager {
  Mono<Grade> getGrade(UUID id);

  Mono<Grade> getSubmissionGrade(UUID submissionId);

  Mono<Grade> updateGrade(UUID id, UserAuth performer, Integer grade, String comment);

  Mono<Page<Grade>> getAllGrades(GradesFilterOptions filterOptions, Pageable pageable);

  Mono<GradesFilterOptions> buildGradesFilterOptions(
      UserAuth performer,
      UUID taskId,
      UUID learnerId,
      Boolean IsGradedByPerformer,
      Integer gradeFrom,
      Integer gradeTo);

  Flux<GradeDB> findByTaskType(TaskType taskType);

  Mono<Long> saveAll(Collection<GradeDB> grades);
}
