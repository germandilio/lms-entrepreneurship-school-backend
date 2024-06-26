package ru.hse.lmsteam.backend.service.tasks;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

public interface HomeworkManager {
  Mono<Homework> findById(UUID id);

  Mono<Map<UUID, Homework>> findByIds(Collection<UUID> ids);

  Flux<Homework> findHomeworksByLesson(UUID lessonId);

  Mono<Homework> create(Homework assignment);

  Mono<Homework> update(Homework assignment);

  Mono<Long> delete(UUID assignmentId);

  Mono<Page<Homework>> findAll(HomeworkFilterOptions filterOptions, Pageable pageable);

  Flux<Homework> getAllPastHomeworks(Instant time);
}
