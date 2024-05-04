package ru.hse.lmsteam.backend.api.v1.controllers;

import java.time.Instant;
import java.util.Objects;
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
import ru.hse.lmsteam.backend.domain.UserAuth;
import ru.hse.lmsteam.backend.domain.UserRole;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicAccessDeniedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicUnauthorizedException;
import ru.hse.lmsteam.backend.service.model.auth.InternalAuthorizationResult;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;
import ru.hse.lmsteam.backend.service.submissions.SubmissionsManager;
import ru.hse.lmsteam.backend.service.user.UserAuthInternal;
import ru.hse.lmsteam.schema.api.submissions.CreateSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmission;
import ru.hse.lmsteam.schema.api.submissions.GetSubmissions;

@RestController
@RequestMapping(
    value = "/api/v1/submissions",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class SubmissionsController implements SubmissionsControllerDocSchema {
  private final UserAuthInternal authorizationManager;
  private final SubmissionsManager submissionsManager;
  private final SubmissionsApiProtoBuilder submissionsApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetSubmission.Response> findById(
      @RequestHeader("Authorization") String rawToken, @PathVariable UUID id) {
    var allowedRoles = Set.of(UserRole.ADMIN);
    return grantAccess(rawToken, allowedRoles)
        .flatMap(
            user ->
                submissionsManager
                    .findById(id)
                    .flatMap(submissionsApiProtoBuilder::buildGetSubmissionResponse));
  }

  @GetMapping
  @Override
  public Mono<GetSubmission.Response> getSubmissionByTaskAndOwner(
      @RequestHeader("Authorization") String rawToken,
      @RequestParam UUID taskId,
      @RequestParam UUID ownerId) {
    var allowedRoles = Set.of(UserRole.ADMIN, UserRole.LEARNER, UserRole.TRACKER);
    return grantAccess(rawToken, allowedRoles)
        .flatMap(
            user -> {
              if (UserRole.LEARNER.equals(user.role()) && !user.userId().equals(ownerId)) {
                return Mono.error(
                    new BusinessLogicAccessDeniedException(
                        "Learner is not allowed to get submission for other users."));
              }
              return submissionsManager
                  .findByTaskAndOwner(taskId, ownerId)
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
    return grantAccess(rawToken, allowedRoles)
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
    return grantAccess(rawToken, allowedRoles)
        .flatMap(
            user -> {
              var publicationDate =
                  request.hasPublishedAt()
                      ? Instant.ofEpochSecond(
                          request.getPublishedAt().getSeconds(),
                          request.getPublishedAt().getNanos())
                      : Instant.now();
              return submissionsManager.upsertSubmission(
                  user.userId(),
                  UUID.fromString(request.getHomeworkId()),
                  publicationDate,
                  request.getPayload());
            })
        .flatMap(submissionsApiProtoBuilder::buildCreateSubmissionResponse);
  }

  private Mono<UserAuth> grantAccess(String rawToken, Set<UserRole> allowedRoles) {
    var token = rawToken.startsWith("Bearer ") ? rawToken.substring(7) : rawToken;
    return authorizationManager
        .tryRetrieveUser(token)
        .flatMap(
            authResult -> {
              if (Objects.requireNonNull(authResult)
                  instanceof InternalAuthorizationResult(UserAuth user)) {
                if (!allowedRoles.contains(user.role())) {
                  return Mono.error(
                      new BusinessLogicAccessDeniedException(
                          "User is not allowed to create submission."));
                }

                return Mono.just(user);
              }
              return Mono.error(
                  new BusinessLogicUnauthorizedException(
                      "Failed to authorize user by given token."));
            });
  }
}
