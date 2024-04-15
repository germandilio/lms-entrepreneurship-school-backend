package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.HomeAssignment;
import ru.hse.lmsteam.backend.service.model.assignments.HomeAssignmentFilterOptions;

public interface HomeAssignmentRepository {
  Mono<HomeAssignment> findById(UUID id);

  Mono<HomeAssignment> update(HomeAssignment homeAssignment);

  Mono<HomeAssignment> create(HomeAssignment homeAssignment);

  Mono<Long> delete(UUID homeAssignmentId);

  Mono<Page<HomeAssignment>> findAll(HomeAssignmentFilterOptions filterOptions, Pageable pageable);
}
