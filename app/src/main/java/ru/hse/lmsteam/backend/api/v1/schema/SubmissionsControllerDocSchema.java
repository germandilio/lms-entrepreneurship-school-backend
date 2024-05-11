package ru.hse.lmsteam.backend.api.v1.schema;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.grades.GetGrade;
import ru.hse.lmsteam.schema.api.submissions.CreateSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmissions;

public interface SubmissionsControllerDocSchema {
  Mono<GetSubmission.Response> findById(String rawToken, UUID id);

  Mono<GetGrade.Response> findBySubmissionId(String token, UUID submissionId);

  Mono<GetSubmission.Response> getSubmissionByTaskAndOwner(
      String rawToken, UUID taskId, UUID ownerId);

  Mono<GetSubmissions.Response> findAll(
      String rawToken, UUID ownerId, UUID teamId, UUID taskId, Pageable pageable);

  Mono<CreateSubmission.Response> createSubmission(
      String rawToken, CreateSubmission.Request request);
}
