package ru.hse.lmsteam.backend.service.lesson;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.repository.LessonRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;
import ru.hse.lmsteam.backend.service.tasks.HomeworkDeleteManager;
import ru.hse.lmsteam.backend.service.tasks.TestDeleteManager;

@Service
@RequiredArgsConstructor
public class LessonManagerImpl implements LessonManager {
  private final HomeworkDeleteManager homeworkManager;
  private final TestDeleteManager testManager;
  private final LessonRepository lessonRepository;

  @Transactional(readOnly = true)
  @Override
  public Mono<Lesson> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return lessonRepository.findById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Map<UUID, Lesson>> findByIds(Collection<UUID> ids) {
    if (ids == null) {
      return Mono.just(Map.of());
    }
    return lessonRepository.findByIds(ids).collectMap(Lesson::id);
  }

  @Transactional
  @Override
  public Mono<Lesson> update(Lesson lesson) {
    if (lesson == null || lesson.id() == null) {
      return Mono.empty();
    }
    return lessonRepository
        .update(lesson)
        .onErrorResume(
            exc -> {
              if (exc instanceof DataIntegrityViolationException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Lesson with the same number already exists!"));
              } else if (exc instanceof TransientDataAccessException) {
                return Mono.error(
                    new BusinessLogicNotFoundException("Lesson with the specified id not found!"));
              }

              return Mono.error(exc);
            });
  }

  @Transactional
  @Override
  public Mono<Lesson> create(Lesson lesson) {
    if (lesson == null) {
      return Mono.empty();
    }
    return lessonRepository
        .create(lesson)
        .onErrorResume(
            exc -> {
              if (exc instanceof DataIntegrityViolationException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Lesson with the same number already exists!"));
              }

              return Mono.error(exc);
            });
  }

  @Transactional
  @Override
  public Mono<Long> delete(UUID lessonId) {
    if (lessonId == null) {
      return Mono.just(0L);
    }

    return Mono.zip(
            homeworkManager.deleteAllByLessonId(lessonId),
            testManager.deleteAllByLessonId(lessonId),
            lessonRepository.delete(lessonId))
        .map(Tuple3::getT3);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Lesson>> findAll(LessonsFilterOptions options, Pageable pageable) {
    if (options == null || pageable == null) {
      return Mono.empty();
    }
    return lessonRepository.findAll(options, pageable);
  }
}
