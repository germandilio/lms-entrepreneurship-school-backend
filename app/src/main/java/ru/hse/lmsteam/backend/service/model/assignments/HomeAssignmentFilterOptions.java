package ru.hse.lmsteam.backend.service.model.assignments;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record HomeAssignmentFilterOptions(
    String title,
    UUID lessonId,
    Instant publishDateFrom,
    Instant publishDateTo,
    Instant deadlineFrom,
    Instant deadlineTo) {}
