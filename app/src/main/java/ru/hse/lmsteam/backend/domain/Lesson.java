package ru.hse.lmsteam.backend.domain;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("lessons")
public record Lesson(
    @Id UUID id, Integer lessonNumber, String title, LocalDate publishDate, byte[] payload) {}
