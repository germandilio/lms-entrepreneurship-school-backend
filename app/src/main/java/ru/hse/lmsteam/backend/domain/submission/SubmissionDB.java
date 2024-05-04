package ru.hse.lmsteam.backend.domain.submission;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("submissions")
@Builder
public record SubmissionDB(
    @With @Id UUID id,
    UUID taskId,
    UUID ownerId,
    UUID publisherId,
    @Nullable UUID teamId,
    Instant submissionDate,
    byte[] payload) {}
