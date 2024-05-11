package ru.hse.lmsteam.backend.service.grades;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.Grade;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

public interface GradesManager {
  Mono<Grade> getGrade(UUID id);

  Mono<Grade> getSubmissionGrade(UUID submissionId);

  Mono<Grade> updateGrade(UUID id, UserAuth performer, Integer grade, String comment);

  Mono<Page<Grade>> getAllGrades(GradesFilterOptions filterOptions, Pageable pageable);
}
