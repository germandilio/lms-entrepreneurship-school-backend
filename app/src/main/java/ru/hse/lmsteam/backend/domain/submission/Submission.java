package ru.hse.lmsteam.backend.domain.submission;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.schema.api.submissions.SubmissionPayload;

@Builder
public record Submission(
    UUID id,
    Homework homework,
    User owner,
    User publisher,
    @Nullable Team team,
    Instant submissionDate,
    SubmissionPayload payload) {}
