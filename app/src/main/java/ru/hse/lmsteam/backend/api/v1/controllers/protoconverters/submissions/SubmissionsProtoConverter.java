package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions;

import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.Submission;

public interface SubmissionsProtoConverter {
  Mono<ru.hse.lmsteam.schema.api.submissions.Submission> convertToProto(Submission submission);
}
