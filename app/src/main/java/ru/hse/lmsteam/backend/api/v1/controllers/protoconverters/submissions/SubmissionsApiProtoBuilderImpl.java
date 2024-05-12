package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.schema.api.submissions.CreateSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmissions;

@Component
@RequiredArgsConstructor
public class SubmissionsApiProtoBuilderImpl implements SubmissionsApiProtoBuilder {
  private final SubmissionsProtoConverter submissionsProtoConverter;
  private final LessonManager lessonManager;

  @Override
  public Mono<CreateSubmission.Response> buildCreateSubmissionResponse(Submission submission) {
    return lessonManager
        .findById(submission.homework().lessonId())
        .singleOptional()
        .map(
            lessonOpt -> {
              var response = CreateSubmission.Response.newBuilder();
              lessonOpt.ifPresent(
                  l ->
                      response.setSubmission(
                          submissionsProtoConverter.convertToProto(submission, l)));
              return response.build();
            });
  }

  @Override
  public Mono<GetSubmission.Response> buildGetSubmissionResponse(Submission submission) {
    return lessonManager
        .findById(submission.homework().lessonId())
        .singleOptional()
        .map(
            lessonOpt -> {
              var response = GetSubmission.Response.newBuilder();
              lessonOpt.ifPresent(
                  l ->
                      response.setSubmission(
                          submissionsProtoConverter.convertToProto(submission, l)));
              return response.build();
            });
  }

  @Override
  public Mono<GetSubmissions.Response> buildGetSubmissionsResponse(Page<Submission> submissions) {
    var lessonF =
        lessonManager.findByIds(
            submissions.stream()
                .map(Submission::homework)
                .map(Homework::lessonId)
                .collect(Collectors.toSet()));

    return lessonF.map(
        lessonsCache -> {
          var protoSubmissions =
              submissions.stream()
                  .map(
                      s ->
                          submissionsProtoConverter.convertToProto(
                              s, lessonsCache.get(s.homework().lessonId())))
                  .toList();
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
