package ru.hse.lmsteam.backend.domain;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("home_assignments")
public record HomeAssignment(
    @Id UUID id,
    UUID lessonId,
    String title,
    Instant publishDate,
    Instant deadlineDate,
    Boolean isGroup,
    byte[] payload) {}
