package ru.hse.lmsteam.backend.domain.lesson;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("lessons")
public record Lesson(
    @With @Id UUID id, Integer lessonNumber, String title, Instant publishDate, byte[] payload) {}
