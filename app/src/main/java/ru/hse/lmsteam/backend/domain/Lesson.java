package ru.hse.lmsteam.backend.domain;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("lessons")
public record Lesson(
    @Id UUID id, Integer lessonNumber, String title, Instant publishDate, byte[] payload) {}
