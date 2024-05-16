package ru.hse.lmsteam.backend.domain.grades;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.backend.domain.tasks.Task;
import ru.hse.lmsteam.backend.domain.user_teams.User;

@Builder
public record Grade(
    UUID id,
    User owner,
    Task task,
    @Nullable Submission submission,
    @Nullable Integer adminGrade,
    @Nullable String adminComment,
    List<TrackerGrade> trackerGradesList) {}
