package ru.hse.lmsteam.backend.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

public interface LessonManager {
  Mono<Lesson> findById(UUID id);

  Mono<Lesson> update(Lesson lesson);

  Mono<Lesson> create(Lesson lesson);

  Mono<Long> delete(UUID lessonId);

  Mono<Page<Lesson>> findAll(LessonsFilterOptions options, Pageable pageable);
}
