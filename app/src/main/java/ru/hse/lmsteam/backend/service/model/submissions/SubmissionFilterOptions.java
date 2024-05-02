package ru.hse.lmsteam.backend.service.model.submissions;

import java.util.UUID;
import lombok.Builder;

@Builder
public record SubmissionFilterOptions(UUID ownerId, UUID teamId, UUID taskId) {}
