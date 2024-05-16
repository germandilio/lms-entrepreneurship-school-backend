package ru.hse.lmsteam.backend.service.submissions;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;
import ru.hse.lmsteam.schema.api.submissions.SubmissionPayload;

public interface SubmissionsManager {
  Mono<Submission> findById(UUID submissionId);

  Mono<Map<UUID, Submission>> findByIds(Collection<UUID> submissionIds);

  Mono<Submission> findByTaskAndOwner(UUID taskId, UUID ownerId);

  Mono<Page<Submission>> findAll(SubmissionFilterOptions filterOptions, Pageable pageable);

  Mono<Submission> upsertSubmission(
      UUID publisherId, UUID taskId, Instant submissionDate, SubmissionPayload payload);
}
