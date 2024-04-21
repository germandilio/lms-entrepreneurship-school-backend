package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Homework;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

public interface HomeAssignmentRepository {
  Mono<Homework> findById(UUID id);

  Mono<Homework> update(Homework homework);

  Mono<Homework> create(Homework homework);

  Mono<Long> delete(UUID homeAssignmentId);

  Mono<Page<Homework>> findAll(HomeworkFilterOptions filterOptions, Pageable pageable);
}
