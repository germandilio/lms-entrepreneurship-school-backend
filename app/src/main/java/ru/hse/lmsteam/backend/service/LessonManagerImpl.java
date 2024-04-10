package ru.hse.lmsteam.backend.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.backend.repository.LessonRepository;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

@Service
@RequiredArgsConstructor
public class LessonManagerImpl implements LessonManager {
  private final LessonRepository lessonRepository;

  @Transactional(readOnly = true)
  @Override
  public Mono<Lesson> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return lessonRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<Lesson> update(Lesson lesson) {
    return null;
  }

  @Transactional
  @Override
  public Mono<Lesson> create(Lesson lesson) {
    return null;
  }

  @Transactional
  @Override
  public Mono<Long> delete(UUID lessonId) {
    if (lessonId == null) {
      return Mono.just(0L);
    }
    return lessonRepository.delete(lessonId);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Lesson>> findAll(LessonsFilterOptions options, Pageable pageable) {
    return null;
  }
}
