package ru.hse.lmsteam.backend.service.model.tasks;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TestFilterOptions(
    String title,
    UUID lessonId,
    Instant publishDateFrom,
    Instant publishDateTo,
    Instant deadlineFrom,
    Instant deadlineTo) {}
