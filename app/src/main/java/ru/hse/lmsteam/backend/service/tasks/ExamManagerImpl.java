package ru.hse.lmsteam.backend.service.tasks;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.backend.repository.impl.tasks.ExamRepository;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicConflictException;
import ru.hse.lmsteam.backend.service.model.tasks.ExamFilterOptions;

@RequiredArgsConstructor
@Service
public class ExamManagerImpl implements ExamManager {
  private final ExamRepository examRepository;

  @Transactional(readOnly = true)
  @Override
  public Mono<Exam> findById(UUID id) {
    if (id == null) {
      return Mono.empty();
    }
    return examRepository.findById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Map<UUID, Exam>> findByIds(Collection<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return Mono.just(Map.of());
    }

    return examRepository.findByIds(ids).collectMap(Exam::id);
  }

  @Transactional
  @Override
  public Mono<Exam> create(Exam assignment) {
    if (assignment == null) {
      return Mono.empty();
    }

    return examRepository
        .create(assignment)
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Exam with the title" + assignment.title() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Exam> update(Exam assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }

    return examRepository
        .update(assignment)
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new BusinessLogicConflictException(
                        "Exam with the title" + assignment.title() + " already exists"));
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
    return examRepository.delete(assignmentId);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Exam>> findAll(ExamFilterOptions filterOptions, Pageable pageable) {
    if (filterOptions == null || pageable == null) {
      return Mono.empty();
    }
    return examRepository.findAll(filterOptions, pageable);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<Exam> getAllPastExams(Instant time) {
    return examRepository.getAllWithDeadlineBefore(time);
  }
}
