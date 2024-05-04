package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.schema.api.submissions.CreateSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmissions;

@Component
@RequiredArgsConstructor
public class SubmissionsApiProtoBuilderImpl implements SubmissionsApiProtoBuilder {
  private final SubmissionsProtoConverter submissionsProtoConverter;

  @Override
  public Mono<CreateSubmission.Response> buildCreateSubmissionResponse(Submission submission) {
    return submissionsProtoConverter
        .convertToProto(submission)
        .map(
            submissionProto -> {
              var response = CreateSubmission.Response.newBuilder();
              response.setSubmission(submissionProto);
              return response.build();
            });
  }

  @Override
  public Mono<GetSubmission.Response> buildGetSubmissionResponse(Submission submission) {
    return submissionsProtoConverter
        .convertToProto(submission)
        .map(
            submissionProto -> {
              var response = GetSubmission.Response.newBuilder();
              response.setSubmission(submissionProto);
              return response.build();
            });
  }

  @Override
  public Mono<GetSubmissions.Response> buildGetSubmissionsResponse(Page<Submission> submissions) {
    return Flux.fromIterable(submissions)
        .flatMap(submissionsProtoConverter::convertToProto)
        .collectList()
        .map(
            protoSubmissions -> {
              var response = GetSubmissions.Response.newBuilder();
              response.setPage(
                  ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                      .setTotalPages(submissions.getTotalPages())
                      .setTotalElements(submissions.getTotalElements())
                      .build());
              response.addAllSubmissions(protoSubmissions);
              return response.build();
            });
  }
}
