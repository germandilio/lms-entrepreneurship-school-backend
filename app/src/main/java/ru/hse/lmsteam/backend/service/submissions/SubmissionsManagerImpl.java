package ru.hse.lmsteam.backend.service.submissions;

import com.google.protobuf.InvalidProtocolBufferException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserRole;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.backend.domain.submission.SubmissionDB;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.repository.SubmissionRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;
import ru.hse.lmsteam.backend.service.tasks.HomeworkManager;
import ru.hse.lmsteam.backend.service.teams.TeamManager;
import ru.hse.lmsteam.backend.service.user.UserManager;
import ru.hse.lmsteam.schema.api.submissions.SubmissionPayload;

@Service
@RequiredArgsConstructor
public class SubmissionsManagerImpl implements SubmissionsManager {
  private final SubmissionRepository submissionRepository;
  private final UserManager userManager;
  private final HomeworkManager homeworkManager;
  private final TeamManager teamManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<Submission> findById(UUID submissionId) {
    if (submissionId == null) {
      return Mono.empty();
    }

    return submissionRepository.findById(submissionId).flatMap(this::buildSubmission);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Submission> findByTaskAndOwner(UUID taskId, UUID ownerId) {
    if (taskId == null || ownerId == null) {
      return Mono.empty();
    }
    return submissionRepository.findByTaskAndOwner(taskId, ownerId).flatMap(this::buildSubmission);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Submission>> findAll(SubmissionFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }

    return submissionRepository
        .findAll(filterOptions, pageable)
        .flatMap(
            page ->
                Flux.fromIterable(page.getContent())
                    .flatMap(this::buildSubmission)
                    .collectList()
                    .map(
                        (submissions) ->
                            new PageImpl<>(
                                submissions, page.getPageable(), page.getTotalElements())));
  }

  @Transactional
  @Override
  public Mono<Submission> upsertSubmission(
      UUID publisherId, UUID taskId, Instant submissionDate, SubmissionPayload payload) {
    return Mono.zip(
            homeworkManager
                .findById(taskId)
                .switchIfEmpty(
                    Mono.error(new BusinessLogicNotFoundException("Task with not found"))),
            submissionRepository
                .findByTaskAndOwner(taskId, publisherId)
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty())),
            userManager
                .findById(publisherId)
                .<User>handle(
                    (user, sink) -> {
                      if (!UserRole.LEARNER.equals(user.role())) {
                        sink.error(
                            new BusinessLogicExpectationFailedException("User is not learner"));
                      }
                      sink.next(user);
                    })
                .switchIfEmpty(
                    Mono.error(new BusinessLogicNotFoundException("User with not found"))))
        .flatMap((t) -> upsertSubmission(t.getT1(), t.getT2(), t.getT3(), submissionDate, payload));
  }

  private Mono<Submission> buildSubmission(SubmissionDB submissionDb) {
    var submissionBuilder = Submission.builder();
    submissionBuilder.id(submissionDb.id());
    submissionBuilder.submissionDate(submissionDb.submissionDate());
    try {
      submissionBuilder.payload(SubmissionPayload.parseFrom(submissionDb.payload()));
    } catch (InvalidProtocolBufferException e) {
      return Mono.error(e);
    }

    var ownerF =
        userManager
            .findById(submissionDb.ownerId())
            .cache(
                (user) -> Duration.ofSeconds(2),
                (throwable) -> Duration.ofMillis(1L),
                () -> Duration.ofMillis(1L));
    var homeworkF = homeworkManager.findById(submissionDb.taskId());
    var groupF =
        homeworkF.flatMap(
            homework -> {
              if (homework.isGroup()) {
                return teamManager.findById(submissionDb.groupId()).map(Optional::of);
              } else return Mono.just(Optional.<Team>empty());
            });

    var publisherF =
        Objects.equals(submissionDb.publisherId(), submissionDb.ownerId())
            ? ownerF
            : userManager.findById(submissionDb.publisherId());

    return Mono.zip(ownerF, homeworkF, groupF, publisherF)
        .doOnNext(
            tuple -> {
              submissionBuilder.owner(tuple.getT1());
              submissionBuilder.homework(tuple.getT2());
              tuple.getT3().ifPresent(submissionBuilder::team);
              submissionBuilder.publisher(tuple.getT4());
            })
        .then(Mono.just(submissionBuilder.build()));
  }

  private Mono<Submission> upsertSubmission(
      Homework hw,
      Optional<SubmissionDB> existingSubmissionOpt,
      User publisher,
      Instant submissionDate,
      SubmissionPayload payload) {
    // get submission by task and owner
    // if group:
    // if exists:
    // update for group
    // else update for owner
    // if not exists:

    // if group submission was on person in 5 group
    // this person transit to 6 group
    // tries to update submission

    //    if (hw.isGroup()) {
    //      if (existingSubmissionOpt.isEmpty()) {
    //        return saveGroupSubmission();
    //      } else {
    //        return updateGroupSubmission();
    //      }
    //    } else {
    //      if (existingSubmissionOpt.isEmpty()) {
    //        return saveIndividualSubmission();
    //      } else {
    //        return updateIndividualSubmission();
    //      }
    //    }

    return null;
  }
}
