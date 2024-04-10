package ru.hse.lmsteam.backend.service.model.lessons;

import java.time.LocalDate;

public record LessonsFilterOptions(Integer lessonNumber, String title, LocalDate publishDate) {}
