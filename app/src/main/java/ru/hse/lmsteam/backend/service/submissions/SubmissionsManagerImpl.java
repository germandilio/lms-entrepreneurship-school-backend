package ru.hse.lmsteam.backend.service.submissions;

import com.google.protobuf.InvalidProtocolBufferException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                buildSubmissions(page.getContent())
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
        .map(
            tuple ->
                buildSubmission(
                    tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), submissionDb));
  }

  private Mono<List<Submission>> buildSubmissions(Collection<SubmissionDB> submissionsDb) {
    var ownersF = userManager.findByIds(submissionsDb.stream().map(SubmissionDB::ownerId).toList());
    var homeworksF =
        homeworkManager.findByIds(submissionsDb.stream().map(SubmissionDB::taskId).toList());
    var groupsF =
        teamManager.findByIds(
            submissionsDb.stream().map(SubmissionDB::groupId).filter(Objects::nonNull).toList());
    var publishersF =
        userManager.findByIds(submissionsDb.stream().map(SubmissionDB::publisherId).toList());
    return Mono.zip(ownersF, homeworksF, groupsF, publishersF)
        .map(
            tuple -> {
              var owners = tuple.getT1();
              var homeworks = tuple.getT2();
              var groups = tuple.getT3();
              var publishers = tuple.getT4();
              return submissionsDb.stream()
                  .map(
                      submissionDb -> {
                        var owner = owners.get(submissionDb.ownerId());
                        var homework = homeworks.get(submissionDb.taskId());
                        var group = Optional.ofNullable(groups.get(submissionDb.groupId()));
                        var publisher = publishers.get(submissionDb.publisherId());
                        return buildSubmission(owner, homework, group, publisher, submissionDb);
                      })
                  .toList();
            });
  }

  private Submission buildSubmission(
      User owner,
      Homework homework,
      Optional<Team> teamOpt,
      User publisher,
      SubmissionDB submissionDB) {
    var submissionBuilder = Submission.builder();
    submissionBuilder.id(submissionDB.id());
    submissionBuilder.submissionDate(submissionDB.submissionDate());
    try {
      submissionBuilder.payload(SubmissionPayload.parseFrom(submissionDB.payload()));
    } catch (InvalidProtocolBufferException e) {
      throw new IllegalStateException("Failed to parse submission payload", e);
    }

    submissionBuilder.owner(owner);
    submissionBuilder.homework(homework);
    teamOpt.ifPresent(submissionBuilder::team);
    submissionBuilder.publisher(publisher);

    return submissionBuilder.build();
  }

  private Mono<Submission> upsertSubmission(
      Homework hw,
      Optional<SubmissionDB> existingSubmissionOpt,
      User publisher,
      Instant submissionDate,
      SubmissionPayload payload) {
    if (!hw.isGroup()) {
      return upsertIndividualSubmission(
          hw, existingSubmissionOpt, publisher, submissionDate, payload);
    }

    return teamManager
        .findTeammates(publisher.id())
        .collectList()
        .flatMap(
            unfilteredTeammates -> {
              if (unfilteredTeammates.isEmpty()) {
                return upsertIndividualSubmission(
                    hw, existingSubmissionOpt, publisher, submissionDate, payload);
              } else {
                return upsertGroupSubmission(
                    unfilteredTeammates,
                    hw,
                    existingSubmissionOpt,
                    publisher,
                    submissionDate,
                    payload);
              }
            });
  }

  private Mono<Submission> upsertIndividualSubmission(
      Homework hw,
      Optional<SubmissionDB> existingSubmissionOpt,
      User publisher,
      Instant submissionDate,
      SubmissionPayload payload) {
    if (existingSubmissionOpt.isPresent()) {
      var submission = existingSubmissionOpt.get();
      if (!submission.ownerId().equals(publisher.id()) || !submission.taskId().equals(hw.id())) {
        return Mono.error(
            new IllegalStateException(
                "Submission already exists and is not owned by the publisher, or is not for the same task"));
      }

      var updatedSubmission =
          SubmissionDB.builder()
              .id(submission.id())
              .ownerId(publisher.id())
              .publisherId(publisher.id())
              .taskId(hw.id())
              // if the submission became individual, we should remove the group id
              .groupId(null)
              .submissionDate(submissionDate)
              .payload(payload.toByteArray())
              .build();
      return submissionRepository.upsert(updatedSubmission).flatMap(this::buildSubmission);
    } else {
      var newSubmission =
          SubmissionDB.builder()
              .ownerId(publisher.id())
              .publisherId(publisher.id())
              .taskId(hw.id())
              .submissionDate(submissionDate)
              .payload(payload.toByteArray())
              .build();
      return submissionRepository.upsert(newSubmission).flatMap(this::buildSubmission);
    }
  }

  private Mono<Submission> upsertGroupSubmission(
      List<User> unfilteredTeammates,
      Homework hw,
      Optional<SubmissionDB> existingSubmissionOpt,
      User publisher,
      Instant submissionDate,
      SubmissionPayload payload) {
    // TODO batch upsert in db
    return null;
  }
}
