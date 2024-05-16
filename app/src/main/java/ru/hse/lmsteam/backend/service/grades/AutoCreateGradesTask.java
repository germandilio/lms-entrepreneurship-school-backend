package ru.hse.lmsteam.backend.service.grades;

import com.google.common.collect.Sets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.hse.lmsteam.backend.domain.grades.GradeDB;
import ru.hse.lmsteam.backend.domain.grades.TaskType;
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

//  @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
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
    // 3) for homeworks get all hws, filter only with deadline in past, find all submissions for
    // them, and get all grades of homeworks type, than:
    // if grade on task for owner don't exist, create it (with submission or not)
    return Mono.just(0L);
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
