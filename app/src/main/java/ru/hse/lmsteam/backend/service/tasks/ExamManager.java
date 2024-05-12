package ru.hse.lmsteam.backend.service.tasks;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.backend.service.model.tasks.ExamFilterOptions;

public interface ExamManager {
  Mono<Exam> findById(UUID id);

  Mono<Map<UUID, Exam>> findByIds(Collection<UUID> ids);

  Mono<Exam> create(Exam assignment);

  Mono<Exam> update(Exam assignment);

  Mono<Long> delete(UUID assignmentId);

  Mono<Page<Exam>> findAll(ExamFilterOptions filterOptions, Pageable pageable);
}
