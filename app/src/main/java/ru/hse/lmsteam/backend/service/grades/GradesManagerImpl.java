package ru.hse.lmsteam.backend.service.grades;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.*;
import ru.hse.lmsteam.backend.domain.submission.Submission;
import ru.hse.lmsteam.backend.domain.tasks.*;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.repository.GradeRepository;
import ru.hse.lmsteam.backend.repository.TrackerGradesRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicAccessDeniedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;
import ru.hse.lmsteam.backend.service.submissions.SubmissionsManager;
import ru.hse.lmsteam.backend.service.tasks.CompetitionManager;
import ru.hse.lmsteam.backend.service.tasks.ExamManager;
import ru.hse.lmsteam.backend.service.tasks.HomeworkManager;
import ru.hse.lmsteam.backend.service.tasks.TestManager;
import ru.hse.lmsteam.backend.service.teams.TeamManager;
import ru.hse.lmsteam.backend.service.user.UserManager;

@Service
@RequiredArgsConstructor
public class GradesManagerImpl implements GradesManager {
  private final GradeRepository gradeRepository;
  private final TrackerGradesRepository trackerGradesRepository;
  private final HomeworkManager homeworkManager;
  private final TestManager testManager;
  private final CompetitionManager competitionManager;
  private final ExamManager examManager;
  private final UserManager userManager;
  private final SubmissionsManager submissionsManager;
  private final TeamManager teamManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<Grade> getGrade(UUID id) {
    if (id == null) {
      return Mono.empty();
    }

    return gradeRepository.findById(id).flatMap(this::buildGrade);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Grade> getSubmissionGrade(UUID submissionId) {
    if (submissionId == null) {
      return Mono.empty();
    }

    return gradeRepository.findBySubmissionId(submissionId).flatMap(this::buildGrade);
  }

  @Transactional
  @Override
  public Mono<Grade> updateGrade(UUID id, UserAuth performer, Integer grade, String comment) {
    if (performer == null || performer.userId() == null) {
      return Mono.error(new IllegalArgumentException("Illegal performer params"));
    }

    return gradeRepository
        .findById(id, true)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Grade not found.")))
        .flatMap(gradeFromDb -> doUpdateGrade(gradeFromDb, performer, grade, comment));
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Grade>> getAllGrades(GradesFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }

    return gradeRepository
        .findAll(filterOptions, pageable)
        .flatMap(
            page ->
                buildGrades(page.getContent())
                    .map(
                        (grades) ->
                            new PageImpl<>(grades, page.getPageable(), page.getTotalElements())));
  }

  @Override
  public Mono<GradesFilterOptions> buildGradesFilterOptions(
      UserAuth performer,
      UUID taskId,
      UUID learnerId,
      Boolean IsGradedByPerformer,
      Integer gradeFrom,
      Integer gradeTo) {
    return getOwnerIdsClause(performer, learnerId)
        .handle(
            (ownersIds, sink) -> {
              var queryBuilder = GradesFilterOptions.builder();
              queryBuilder.ownersId(ownersIds);

              queryBuilder.gradeFrom(gradeFrom);
              queryBuilder.gradeTo(gradeTo);
              queryBuilder.taskId(taskId);

              if (IsGradedByPerformer != null) {
                if (!Set.of(UserRole.TRACKER, UserRole.ADMIN).contains(performer.role())) {
                  sink.error(new BusinessLogicAccessDeniedException("Illegal performer role"));
                  return;
                }

                if (UserRole.ADMIN.equals(performer.role())) {
                  queryBuilder.gradedByAdmin(IsGradedByPerformer);
                }
                if (UserRole.TRACKER.equals(performer.role())) {
                  queryBuilder.gradedByTrackerId(performer.userId());
                }
              }

              if (UserRole.TRACKER.equals(performer.role())) {
                queryBuilder.taskType(TaskType.HOMEWORK);
              }

              sink.next(queryBuilder.build());
            });
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<GradeDB> findByTaskType(TaskType taskType) {
    return gradeRepository.findByTaskType(taskType);
  }

  @Transactional
  @Override
  public Mono<Long> saveAll(Collection<GradeDB> grades) {
    if (grades.isEmpty()) {
      return Mono.just(0L);
    }
    return gradeRepository.saveAll(grades).switchIfEmpty(Mono.just(0L));
  }

  private Mono<Collection<UUID>> getOwnerIdsClause(UserAuth performer, UUID learnerId) {
    if (UserRole.LEARNER.equals(performer.role())) {
      // restrict access for only own grades for learner
      return Mono.just(Sets.intersection(Set.of(performer.userId()), Set.of(learnerId)));
    }

    if (UserRole.ADMIN.equals(performer.role())) {
      return Mono.just(Collections.singleton(learnerId));
    }

    if (UserRole.TRACKER.equals(performer.role())) {
      return teamManager
          .findTeammates(performer.userId())
          .filter(user -> UserRole.LEARNER.equals(user.role()))
          .map(User::id)
          .collect(ImmutableSet.toImmutableSet());
    }

    return Mono.just(Collections.emptySet());
  }

  private Mono<Grade> doUpdateGrade(
      GradeDB gradeFromDb, UserAuth performer, Integer grade, String comment) {
    if (UserRole.ADMIN.equals(performer.role())) {
      var updatedGrade =
          GradeDB.builder()
              .id(gradeFromDb.id())
              .taskId(gradeFromDb.taskId())
              .submissionId(gradeFromDb.submissionId())
              .ownerId(gradeFromDb.ownerId())
              .taskType(gradeFromDb.taskType())
              .adminGrade(grade)
              .adminComment(comment)
              .build();
      return gradeRepository.upsert(updatedGrade).flatMap(this::buildGrade);
    } else if (UserRole.TRACKER.equals(performer.role())) {
      var updatedTrackerGrade =
          TrackerGradeDb.builder()
              .trackerId(performer.userId())
              .gradeId(gradeFromDb.id())
              .grade(grade)
              .comment(comment)
              .build();

      return trackerGradesRepository
          .upsert(updatedTrackerGrade)
          .flatMap(trackerGrade -> gradeRepository.findById(trackerGrade.gradeId()))
          .flatMap(this::buildGrade);
    } else {
      return Mono.error(new BusinessLogicAccessDeniedException("Illegal performer role"));
    }
  }

  /**
   * Build grade domain model based on database entity with ids references.
   *
   * @param gradeDB grade database entity
   * @return grade domain model
   */
  private Mono<Grade> buildGrade(GradeDB gradeDB) {
    return Mono.zip(
            getTask().apply(gradeDB),
            userManager.findById(gradeDB.ownerId()),
            submissionsManager
                .findById(gradeDB.submissionId())
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty())),
            trackerGradesRepository.findByGradeId(gradeDB.id()).collectList())
        .flatMap(
            tuple ->
                userManager
                    .findByIds(
                        tuple.getT4().stream()
                            .map(TrackerGradeDb::trackerId)
                            .collect(Collectors.toSet()))
                    .map(
                        trackers ->
                            buildGrade(
                                gradeDB,
                                tuple.getT2(),
                                tuple.getT1(),
                                tuple.getT3(),
                                tuple.getT4().stream()
                                    .map(
                                        trackerGradeDb ->
                                            buildTrackerGrade(
                                                trackerGradeDb,
                                                trackers.get(trackerGradeDb.trackerId())))
                                    .collect(Collectors.toList()))));
  }

  /**
   * Build grades domain model based on database entities with ids references. Batch operation.
   *
   * @param gradesDB grades database entities
   * @return grades domain models
   */
  private Mono<List<Grade>> buildGrades(Collection<GradeDB> gradesDB) {
    var tasksCacheF = buildTasksCache(gradesDB);
    var trackerGradesF =
        getTrackerGradesGrouped(gradesDB.stream().map(GradeDB::id).collect(Collectors.toSet()));

    var trackersF =
        trackerGradesF
            .map(
                tGrades ->
                    tGrades.values().stream()
                        .flatMap(grades -> grades.stream().map(TrackerGradeDb::trackerId))
                        .collect(Collectors.toSet()))
            .flatMap(userManager::findByIds);
    var usersF =
        userManager.findByIds(gradesDB.stream().map(GradeDB::ownerId).collect(Collectors.toSet()));

    var submissionsF =
        submissionsManager.findByIds(
            gradesDB.stream()
                .map(GradeDB::submissionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

    return Mono.zip(tasksCacheF, trackerGradesF, trackersF, usersF, submissionsF)
        .map(
            tuple -> {
              var buildingGradeFun =
                  buildGradesFun(
                      tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4(), tuple.getT5());

              return gradesDB.stream().map(buildingGradeFun).collect(Collectors.toList());
            });
  }

  @NotNull
  private Function<GradeDB, Grade> buildGradesFun(
      Function<GradeDB, Optional<Task>> tasksCache,
      Map<UUID, Collection<TrackerGradeDb>> trackerGrades,
      Map<UUID, User> trackers,
      Map<UUID, User> users,
      Map<UUID, Submission> submissions) {
    return gradeDB -> {
      var task =
          tasksCache
              .apply(gradeDB)
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Task not found for gradeId: "
                              + gradeDB.id()
                              + ", taskId: "
                              + gradeDB.taskId()
                              + ", taskType: "
                              + gradeDB.taskType()));
      var owner = users.get(gradeDB.ownerId());
      var submission =
          gradeDB.submissionId() != null
              ? Optional.of(submissions.get(gradeDB.submissionId()))
              : Optional.<Submission>empty();
      var trackerGradesList =
          trackerGrades.get(gradeDB.id()).stream()
              .map(
                  trackerGradeDb ->
                      buildTrackerGrade(trackerGradeDb, trackers.get(trackerGradeDb.trackerId())))
              .collect(Collectors.toList());

      return buildGrade(gradeDB, owner, task, submission, trackerGradesList);
    };
  }

  /**
   * Find Tracker grades by gradeIds (batch operation).
   *
   * @param gradeIds gradeIds to find
   * @return Tracker grades grouped by gradeId (grade)
   */
  private Mono<Map<UUID, Collection<TrackerGradeDb>>> getTrackerGradesGrouped(
      Collection<UUID> gradeIds) {
    return trackerGradesRepository
        .findByGradeIds(gradeIds)
        .collectMultimap(TrackerGradeDb::gradeId);
  }

  private Grade buildGrade(
      GradeDB gradeDB,
      User owner,
      Task task,
      Optional<Submission> submission,
      List<TrackerGrade> trackerGrades) {
    return Grade.builder()
        .id(gradeDB.id())
        .owner(owner)
        .task(task)
        .submission(submission.orElse(null))
        .adminGrade(gradeDB.adminGrade())
        .adminComment(gradeDB.adminComment())
        .trackerGradesList(trackerGrades)
        .build();
  }

  private TrackerGrade buildTrackerGrade(TrackerGradeDb gradeDb, User tracker) {
    if (tracker == null) throw new IllegalArgumentException("Tracker not found!");
    return TrackerGrade.builder()
        .tracker(tracker)
        .trackerGrade(gradeDb.grade())
        .trackerComment(gradeDb.comment())
        .build();
  }

  private Function<GradeDB, Mono<Task>> getTask() {
    return (gradeDb) ->
        switch (gradeDb.taskType()) {
          case HOMEWORK -> homeworkManager.findById(gradeDb.taskId()).map(Task.class::cast);
          case TEST -> testManager.findById(gradeDb.taskId()).map(Task.class::cast);
          case COMPETITION -> competitionManager.findById(gradeDb.taskId()).map(Task.class::cast);
          case EXAM -> examManager.findById(gradeDb.taskId()).map(Task.class::cast);
        };
  }

  /**
   * Build cache for tasks, based on tasks information from gradeDbs. Supports different task types,
   * based on taskType field of gradeDb.
   *
   * @return function to get task by gradeId (in-memory cache).
   */
  private Mono<Function<GradeDB, Optional<Task>>> buildTasksCache(Collection<GradeDB> grades) {
    var homeworkIds = taskIdsByType().apply(grades, TaskType.HOMEWORK);
    var testIds = taskIdsByType().apply(grades, TaskType.TEST);
    var competitionIds = taskIdsByType().apply(grades, TaskType.COMPETITION);
    var examIds = taskIdsByType().apply(grades, TaskType.EXAM);
    return Mono.zip(
            homeworkManager.findByIds(homeworkIds),
            testManager.findByIds(testIds),
            competitionManager.findByIds(competitionIds),
            examManager.findByIds(examIds))
        .map(tResult -> getTask(tResult.getT1(), tResult.getT2(), tResult.getT3(), tResult.getT4()))
        .switchIfEmpty(
            Mono.error(
                new IllegalStateException("Cannot build task cache, (zip on empty tasks list)")));
  }

  /**
   * Get tasks from computed cache, based on taskType by gradeId.
   *
   * @return Task (Homework, Exam. Test, Competition)
   */
  private Function<GradeDB, Optional<Task>> getTask(
      Map<UUID, Homework> homeworkCache,
      Map<UUID, Test> testCache,
      Map<UUID, Competition> competitionCache,
      Map<UUID, Exam> examCache) {
    return (gradeDB) -> {
      if (gradeDB == null || gradeDB.id() == null) {
        throw new IllegalArgumentException("gradeId is null");
      }

      var result =
          switch (gradeDB.taskType()) {
            case HOMEWORK -> homeworkCache.get(gradeDB.taskId());
            case TEST -> testCache.get(gradeDB.taskId());
            case COMPETITION -> competitionCache.get(gradeDB.taskId());
            case EXAM -> examCache.get(gradeDB.taskId());
          };
      return Optional.ofNullable(result);
    };
  }

  private BiFunction<Collection<GradeDB>, TaskType, Set<UUID>> taskIdsByType() {
    return (grades, taskType) ->
        grades.stream()
            .filter(gdb -> taskType.equals(gdb.taskType()))
            .map(GradeDB::taskId)
            .collect(Collectors.toSet());
  }
}
