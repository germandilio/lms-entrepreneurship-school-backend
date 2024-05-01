package ru.hse.lmsteam.backend.service.model.submissions;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("submissions")
@Builder
public record SubmissionDB(
    @Id UUID id,
    UUID taskId,
    UUID ownerId,
    UUID publisherId,
    UUID groupId,
    Instant submissionDate,
    byte[] payload) {}
