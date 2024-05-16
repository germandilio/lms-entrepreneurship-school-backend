package ru.hse.lmsteam.backend.service.tasks;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.repository.SubmissionRepository;
import ru.hse.lmsteam.backend.repository.impl.tasks.TestRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.backend.service.model.tasks.TestFilterOptions;

@Service
@RequiredArgsConstructor
public class TestManagerImpl implements TestManager {
  private final SubmissionRepository submissionRepository;
  private final TestRepository testRepository;
  private final LessonManager lessonManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<Test> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return testRepository.findById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Map<UUID, Test>> findByIds(Collection<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.just(Map.of());
    }

    return testRepository.findByIds(ids).collectMap(Test::id);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<Test> findTestsByLesson(UUID lessonId) {
    if (lessonId == null) {
      return Flux.empty();
    }

    return testRepository.findTasksByLesson(lessonId);
  }

  @Transactional
  @Override
  public Mono<Test> create(Test assignment) {
    if (assignment == null) {
      return Mono.empty();
    }

    return lessonManager
        .findById(assignment.lessonId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Lesson not found!")))
        .then(testRepository.create(assignment))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Test with the title" + assignment.title() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Test> update(Test assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }

    return lessonManager
        .findById(assignment.lessonId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Lesson not found!")))
        .then(testRepository.update(assignment))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Test with the title" + assignment.title() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Long> delete(UUID assignmentId) {
    if (assignmentId == null) {
      return Mono.just(0L);
    }
    return submissionRepository
        .deleteAllByTaskIds(List.of(assignmentId))
        .then(testRepository.delete(assignmentId));
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Test>> findAll(TestFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }
    return testRepository.findAll(filterOptions, pageable);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<Test> getAllPastTests(Instant time) {
    return testRepository.getAllWithDeadlineBefore(time);
  }
}
