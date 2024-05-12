package ru.hse.lmsteam.backend.domain.tasks;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("homeworks")
public record Homework(
    @With @Id UUID id,
    UUID lessonId,
    String title,
    Instant publishDate,
    Instant deadlineDate,
    Boolean isGroup,
    byte[] payload)
    implements Task {}
