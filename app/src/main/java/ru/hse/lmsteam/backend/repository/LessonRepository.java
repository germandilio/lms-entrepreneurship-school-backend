package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

public interface LessonRepository {
  Mono<Lesson> findById(UUID id);

  Mono<Lesson> update(Lesson lesson);

  Mono<Lesson> create(Lesson lesson);

  Mono<Long> delete(UUID lessonId);

  Mono<Page<Lesson>> findAll(LessonsFilterOptions filterOptions, Pageable pageable);
}
