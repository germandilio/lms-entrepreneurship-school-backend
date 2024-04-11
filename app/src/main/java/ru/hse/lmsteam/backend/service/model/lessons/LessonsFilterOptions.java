package ru.hse.lmsteam.backend.service.model.lessons;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record LessonsFilterOptions(Integer lessonNumber, String title, LocalDate publishDate) {}
