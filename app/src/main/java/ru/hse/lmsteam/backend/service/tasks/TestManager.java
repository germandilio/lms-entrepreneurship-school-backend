package ru.hse.lmsteam.backend.service.tasks;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.service.model.tasks.TestFilterOptions;

public interface TestManager {
  Mono<Test> findById(UUID id);

  Mono<Map<UUID, Test>> findByIds(Collection<UUID> ids);

  Flux<Test> findTestsByLesson(UUID lessonId);

  Mono<Test> create(Test assignment);

  Mono<Test> update(Test assignment);

  Mono<Long> delete(UUID assignmentId);

  Mono<Page<Test>> findAll(TestFilterOptions filterOptions, Pageable pageable);

  Flux<Test> getAllPastTests(Instant time);
}
