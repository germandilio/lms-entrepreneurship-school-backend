package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.backend.repository.impl.tasks.ExamRepository;
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

  @Transactional
  @Override
  public Mono<Exam> create(Exam assignment) {
    if (assignment == null) {
      return Mono.empty();
    }

    return examRepository.create(assignment);
  }

  @Transactional
  @Override
  public Mono<Exam> update(Exam assignment) {
    if (assignment == null || assignment.id() == null) {
      return Mono.empty();
    }

    return examRepository.update(assignment);
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
}
