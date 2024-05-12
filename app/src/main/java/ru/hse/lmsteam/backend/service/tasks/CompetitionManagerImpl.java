package ru.hse.lmsteam.backend.service.tasks;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.backend.repository.impl.tasks.CompetitionRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
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

  @Transactional(readOnly = true)
  @Override
  public Mono<Map<UUID, Competition>> findByIds(Collection<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.just(Map.of());
    }

    return competitionRepository.findByIds(ids).collectMap(Competition::id);
  }

  @Transactional
  @Override
  public Mono<Competition> create(Competition assignment) {
    if (assignment == null) {
      return Mono.empty();
    }

    return competitionRepository
        .create(assignment)
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Competition with the title" + assignment.title() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Competition> update(Competition assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }

    return competitionRepository
        .update(assignment)
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Competition with the title" + assignment.title() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
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
