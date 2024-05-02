package ru.hse.lmsteam.backend.service.model.lessons;

import java.time.Instant;
import lombok.Builder;

@Builder
public record LessonsFilterOptions(
    Integer lessonNumber, String title, Instant publishDateFrom, Instant publishDateTimeTo) {}
