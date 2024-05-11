package ru.hse.lmsteam.backend.service.grades;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.Grade;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

import java.util.UUID;

public class GradesManagerImpl implements GradesManager {
    @Override
    public Mono<Grade> getGrade(UUID id) {
    return null;
    }

    @Override
    public Mono<Grade> getSubmissionGrade(UUID submissionId) {
        return null;
    }

    @Override
    public Mono<Grade> updateGrade(UUID id, UserAuth performer, Integer grade, String comment) {
        return null;
    }

    @Override
    public Mono<Page<Grade>> getAllGrades(GradesFilterOptions filterOptions, Pageable pageable) {
        return null;
    }
}
