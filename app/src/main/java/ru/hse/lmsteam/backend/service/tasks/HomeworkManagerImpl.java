package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.repository.impl.tasks.HomeworkRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

@Service
@RequiredArgsConstructor
public class HomeworkManagerImpl implements HomeworkManager {
  private final HomeworkRepository homeworkRepository;
  private final LessonManager lessonManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<Homework> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return homeworkRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<Homework> create(Homework assignment) {
    if (assignment == null) {
      return Mono.empty();
    }

    return lessonManager
        .findById(assignment.lessonId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Lesson not found!")))
        .then(homeworkRepository.create(assignment))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Homework with the title" + assignment.title() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Homework> update(Homework assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }

    return lessonManager
        .findById(assignment.lessonId())
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Lesson not found!")))
        .then(homeworkRepository.update(assignment))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Homework with the title" + assignment.title() + " already exists"));
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
    return homeworkRepository.delete(assignmentId);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Homework>> findAll(HomeworkFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }
    return homeworkRepository.findAll(filterOptions, pageable);
  }
}
