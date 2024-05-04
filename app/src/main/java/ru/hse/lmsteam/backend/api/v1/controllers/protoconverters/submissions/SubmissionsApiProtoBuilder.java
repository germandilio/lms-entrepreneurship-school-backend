package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions;

import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.schema.api.submissions.CreateSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmissions;

public interface SubmissionsApiProtoBuilder {
  Mono<CreateSubmission.Response> buildCreateSubmissionResponse(Submission submission);

  Mono<GetSubmission.Response> buildGetSubmissionResponse(Submission submission);

  Mono<GetSubmissions.Response> buildGetSubmissionsResponse(Page<Submission> submissions);
}
