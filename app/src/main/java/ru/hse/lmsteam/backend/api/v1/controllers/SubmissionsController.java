package ru.hse.lmsteam.backend.api.v1.controllers;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions.SubmissionsApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.SubmissionsControllerDocSchema;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicAccessDeniedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;
import ru.hse.lmsteam.backend.service.submissions.SubmissionsManager;
import ru.hse.lmsteam.schema.api.grades.GetGrade;
import ru.hse.lmsteam.schema.api.submissions.CreateSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmissions;

@RestController
@RequestMapping(
    value = "/api/v1/submissions",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class SubmissionsController implements SubmissionsControllerDocSchema {
  private final GrantAccessUtils grantAccessUtils;
  private final SubmissionsManager submissionsManager;
  private final SubmissionsApiProtoBuilder submissionsApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetSubmission.Response> findById(
      @RequestHeader("Authorization") String rawToken, @PathVariable UUID id) {
    var allowedRoles = Set.of(UserRole.ADMIN);
    return grantAccessUtils
        .grantAccess(rawToken, allowedRoles)
        .flatMap(
            user ->
                submissionsManager
                    .findById(id)
                    .switchIfEmpty(
                        Mono.error(new BusinessLogicNotFoundException("Submission not found.")))
                    .flatMap(submissionsApiProtoBuilder::buildGetSubmissionResponse));
  }

  @GetMapping("/{submissionId}/grade")
  @Override
  public Mono<GetGrade.Response> findBySubmissionId(
      @RequestHeader("Authorization") String rawToken, @PathVariable UUID submissionId) {
    return null;
  }

  @GetMapping
  @Override
  public Mono<GetSubmission.Response> getSubmissionByTaskAndOwner(
      @RequestHeader("Authorization") String rawToken,
      @RequestParam UUID taskId,
      @RequestParam UUID ownerId) {
    var allowedRoles = Set.of(UserRole.ADMIN, UserRole.LEARNER, UserRole.TRACKER);
    return grantAccessUtils
        .grantAccess(rawToken, allowedRoles)
        .flatMap(
            user -> {
              if (UserRole.LEARNER.equals(user.role()) && !user.userId().equals(ownerId)) {
                return Mono.error(
                    new BusinessLogicAccessDeniedException(
                        "Learner is not allowed to get submission for other users."));
              }
              return submissionsManager
                  .findByTaskAndOwner(taskId, ownerId)
                  .switchIfEmpty(
                      Mono.error(new BusinessLogicNotFoundException("Submission not found.")))
                  .flatMap(submissionsApiProtoBuilder::buildGetSubmissionResponse);
            });
  }

  @GetMapping("/list")
  @PageableAsQueryParam
  @Override
  public Mono<GetSubmissions.Response> findAll(
      @RequestHeader("Authorization") String rawToken,
      @RequestParam(required = false) UUID ownerId,
      @RequestParam(required = false) UUID teamId,
      @RequestParam(required = false) UUID taskId,
      Pageable pageable) {
    var allowedRoles = Set.of(UserRole.ADMIN);
    return grantAccessUtils
        .grantAccess(rawToken, allowedRoles)
        .flatMap(
            user -> {
              var filterOptions = new SubmissionFilterOptions(ownerId, teamId, taskId);
              return submissionsManager
                  .findAll(filterOptions, pageable)
                  .flatMap(submissionsApiProtoBuilder::buildGetSubmissionsResponse);
            });
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateSubmission.Response> createSubmission(
      @RequestHeader("Authorization") String rawToken,
      @RequestBody CreateSubmission.Request request) {
    var allowedRoles = Set.of(UserRole.LEARNER);
    return grantAccessUtils
        .grantAccess(rawToken, allowedRoles)
        .flatMap(
            user -> {
              var publicationDate = Instant.now();
              return submissionsManager.upsertSubmission(
                  user.userId(),
                  UUID.fromString(request.getHomeworkId()),
                  publicationDate,
                  request.getPayload());
            })
        .flatMap(submissionsApiProtoBuilder::buildCreateSubmissionResponse);
  }
}
