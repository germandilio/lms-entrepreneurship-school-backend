package ru.hse.lmsteam.backend.service.assignments;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.HomeAssignment;
import ru.hse.lmsteam.backend.service.model.assignments.HomeAssignmentFilterOptions;

public interface HomeAssignmentManager {
  Mono<HomeAssignment> findById(UUID id);

  Mono<HomeAssignment> create(HomeAssignment assignment);

  Mono<HomeAssignment> upsert(HomeAssignment assignment);

  Mono<Long> delete(UUID assignmentId);

  Mono<Page<HomeAssignment>> findAll(HomeAssignmentFilterOptions filterOptions, Pageable pageable);
}
