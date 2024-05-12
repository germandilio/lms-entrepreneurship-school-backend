package ru.hse.lmsteam.backend.service.lesson;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

public interface LessonManager {
  Mono<Lesson> findById(UUID id);

  Mono<Map<UUID, Lesson>> findByIds(Collection<UUID> ids);

  Mono<Lesson> update(Lesson lesson);

  Mono<Lesson> create(Lesson lesson);

  Mono<Long> delete(UUID lessonId);

  Mono<Page<Lesson>> findAll(LessonsFilterOptions options, Pageable pageable);
}
