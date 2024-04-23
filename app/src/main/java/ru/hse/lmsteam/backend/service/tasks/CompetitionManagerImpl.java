package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.backend.repository.impl.tasks.CompetitionRepository;
import ru.hse.lmsteam.backend.service.model.tasks.CompetitionFilterOptions;

@RequiredArgsConstructor
@Service
public class CompetitionManagerImpl implements CompetitionManager {
  private final CompetitionRepository competitionRepository;

  @Transactional(readOnly = true)
  @Override
  public Mono<Competition> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return competitionRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<Competition> create(Competition assignment) {
    if (assignment == null) {
      return Mono.empty();
    }

    return competitionRepository.create(assignment);
  }

  @Transactional
  @Override
  public Mono<Competition> update(Competition assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }

    return competitionRepository.update(assignment);
  }

  @Transactional
  @Override
  public Mono<Long> delete(UUID assignmentId) {
    if (assignmentId == null) {
      return Mono.just(0L);
    }
    return competitionRepository.delete(assignmentId);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Competition>> findAll(
      CompetitionFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }
    return competitionRepository.findAll(filterOptions, pageable);
  }
}
