package ru.hse.lmsteam.backend.service.grades;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.hse.lmsteam.backend.domain.grades.GradeDB;
import ru.hse.lmsteam.backend.domain.grades.TaskType;
import ru.hse.lmsteam.backend.domain.submission.SubmissionDB;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.domain.tasks.Task;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;
import ru.hse.lmsteam.backend.service.submissions.SubmissionsManager;
import ru.hse.lmsteam.backend.service.tasks.CompetitionManager;
import ru.hse.lmsteam.backend.service.tasks.ExamManager;
import ru.hse.lmsteam.backend.service.tasks.HomeworkManager;
import ru.hse.lmsteam.backend.service.tasks.TestManager;
import ru.hse.lmsteam.backend.service.user.UserManager;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoCreateGradesTask {
  private final GradesManager gradesManager;

  private final UserManager userManager;
  private final SubmissionsManager submissionsManager;

  private final ExamManager examManager;
  private final HomeworkManager homeworkManager;
  private final TestManager testManager;
  private final CompetitionManager competitionManager;

  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
  public void upsertGradesTask() {
    upsertGrades().subscribeOn(Schedulers.immediate()).subscribe();
  }

  private Mono<Void> upsertGrades() {
    var startTime = Instant.now();
    log.info(
        "Start upsert grades task, time (UTC): {}",
        LocalDateTime.ofInstant(startTime, ZoneId.of("UTC")));

    return userManager
        .findAll(UserFilterOptions.builder().isDeleted(false).build(), Pageable.ofSize(1000))
        .map(Page::getContent)
        .flatMap(
            nonDeletedUsers ->
                Mono.zip(
                    upsertExamGrades(nonDeletedUsers, startTime),
                        upsertTestGrades(nonDeletedUsers, startTime),
                    upsertCompetitionGrades(nonDeletedUsers, startTime),
                        upsertHomeworkGrades(nonDeletedUsers, startTime)))
        .map(
            t -> {
              log.info("Upsert grades task finished");
              log.info("Upserted {} exam grades", t.getT1());
              log.info("Upserted {} test grades", t.getT2());
              log.info("Upserted {} competition grades", t.getT3());
              log.info("Upserted {} homework grades", t.getT4());
              return t;
            })
        .then();
  }

  private Mono<Long> upsertExamGrades(List<User> users, Instant startTime) {
    return examManager
        .getAllPastExams(startTime)
        .collectList()
        .zipWith(gradesManager.findByTaskType(TaskType.EXAM).collectList())
        .flatMap(t -> createNewGrades(users, t.getT1(), t.getT2()));
  }

  private Mono<Long> upsertTestGrades(List<User> users, Instant startTime) {
    return testManager
        .getAllPastTests(startTime)
        .collectList()
        .zipWith(gradesManager.findByTaskType(TaskType.TEST).collectList())
        .flatMap(t -> createNewGrades(users, t.getT1(), t.getT2()));
  }

  private Mono<Long> upsertCompetitionGrades(List<User> users, Instant startTime) {
    return competitionManager
        .getAllPastCompetitions(startTime)
        .collectList()
        .zipWith(gradesManager.findByTaskType(TaskType.COMPETITION).collectList())
        .flatMap(t -> createNewGrades(users, t.getT1(), t.getT2()));
  }

  private Mono<Long> upsertHomeworkGrades(List<User> users, Instant startTime) {
    var homeworksF = homeworkManager.getAllPastHomeworks(startTime).collectList();
    var submissionsF =
        submissionsManager.getALlSubmissions().collectMultimap(SubmissionDB::ownerId);
    var gradesF = gradesManager.getAll().collectMultimap(GradeDB::taskId);
    return Mono.zip(homeworksF, submissionsF, gradesF)
        .flatMap(
            t ->
                createNewGradesForHometasks(
                    users,
                    t.getT3(),
                    t.getT1(),
                    t.getT2().entrySet().stream()
                        .map(
                            entry ->
                                Map.entry(
                                    entry.getKey(),
                                    entry.getValue().stream()
                                        .collect(
                                            ImmutableMap.toImmutableMap(
                                                SubmissionDB::taskId, Functions.identity()))))
                        .collect(
                            ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue))));
  }

  private Mono<Long> createNewGradesForHometasks(
      List<User> users,
      Map<UUID, Collection<GradeDB>> existingGradesByTask,
      List<Homework> homeworks,
      Map<UUID, Map<UUID, SubmissionDB>> submissionsByUserByTask) {
    final var getSubmission = getSubmissionIdNullable(submissionsByUserByTask);
    // for task
    // for users
    // if not exist create
    return Flux.fromIterable(homeworks)
        .flatMap(
            hw -> {
              var existingGradesByUser =
                  existingGradesByTask.get(hw.id()).stream()
                      .collect(ImmutableMap.toImmutableMap(GradeDB::ownerId, Function.identity()));
              var updatedGrades =
                  users.stream()
                      .map(
                          u -> {
                            if (!existingGradesByUser.containsKey(u.id())) {
                              return GradeDB.builder()
                                  .ownerId(u.id())
                                  .taskId(hw.id())
                                  .taskType(TaskType.HOMEWORK)
                                  .submissionId(getSubmission.apply(u.id(), hw.id()))
                                  .build();
                            } else {
                              return Objects.requireNonNull(existingGradesByUser.get(u.id()))
                                  .withSubmissionId(getSubmission.apply(u.id(), hw.id()));
                            }
                          })
                      .toList();

              return gradesManager.saveAll(updatedGrades);
            })
        .reduce(Long::sum);
  }

  private BiFunction<UUID, UUID, UUID> getSubmissionIdNullable(
      Map<UUID, Map<UUID, SubmissionDB>> submissionsByUserByTask) {
    return (UUID userId, UUID homeworkId) -> {
      var userTasks = submissionsByUserByTask.get(userId);
      if (userTasks == null) return null;
      var submission = userTasks.get(homeworkId);
      if (submission == null) return null;
      return submission.id();
    };
  }

  private <T extends Task> Mono<Long> createNewGrades(
      List<User> allUsers, List<T> tasks, List<GradeDB> existingGrades) {
    var newGrades =
        tasks.stream()
            .flatMap(
                test -> {
                  // for every exam (task_id) find all allUsers, which don't have grade for
                  var nonGradedUsersForExam =
                      Sets.difference(
                          allUsers.stream().map(User::id).collect(Collectors.toSet()),
                          existingGrades.stream()
                              .filter(g -> g.taskId().equals(test.id()))
                              .map(GradeDB::ownerId)
                              .collect(Collectors.toSet()));
                  return nonGradedUsersForExam.stream()
                      .map(
                          userId ->
                              GradeDB.builder()
                                  .ownerId(userId)
                                  .taskId(test.id())
                                  .taskType(TaskType.EXAM)
                                  .build());
                })
            .toList();
    return gradesManager.saveAll(newGrades);
  }
}
