package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.service.model.tasks.TestFilterOptions;

public interface TestManager {
  Mono<Test> findById(UUID id);

  Mono<Test> create(Test assignment);

  Mono<Test> update(Test assignment);

  Mono<Long> delete(UUID assignmentId);

  Mono<Page<Test>> findAll(TestFilterOptions filterOptions, Pageable pageable);
}
