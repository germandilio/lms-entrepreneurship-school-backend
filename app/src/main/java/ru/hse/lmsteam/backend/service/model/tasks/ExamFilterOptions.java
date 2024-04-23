package ru.hse.lmsteam.backend.service.model.tasks;

import java.time.Instant;
import lombok.Builder;

@Builder
public record ExamFilterOptions(
    String title,
    Instant publishDateFrom,
    Instant publishDateTo,
    Instant deadlineFrom,
    Instant deadlineTo) {}
