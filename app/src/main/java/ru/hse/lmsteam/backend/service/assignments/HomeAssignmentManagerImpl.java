package ru.hse.lmsteam.backend.service.assignments;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.HomeAssignment;
import ru.hse.lmsteam.backend.repository.HomeAssignmentRepository;
import ru.hse.lmsteam.backend.service.model.assignments.HomeAssignmentFilterOptions;

@Service
@RequiredArgsConstructor
public class HomeAssignmentManagerImpl implements HomeAssignmentManager {
  private final HomeAssignmentRepository homeAssignmentRepository;

  @Transactional(readOnly = true)
  @Override
  public Mono<HomeAssignment> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return homeAssignmentRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<HomeAssignment> create(HomeAssignment assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }
    return homeAssignmentRepository.create(assignment);
  }

  @Transactional
  @Override
  public Mono<HomeAssignment> upsert(HomeAssignment assignment) {
    if (assignment == null) {
      return Mono.empty();
    }
    return homeAssignmentRepository.update(assignment);
  }

  @Transactional
  @Override
  public Mono<Long> delete(UUID assignmentId) {
    if (assignmentId == null) {
      return Mono.just(0L);
    }
    return homeAssignmentRepository.delete(assignmentId);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<HomeAssignment>> findAll(
      HomeAssignmentFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }
    return homeAssignmentRepository.findAll(filterOptions, pageable);
  }
}
