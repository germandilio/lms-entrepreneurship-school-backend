package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.SubmissionDB;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;

public interface SubmissionRepository {
  Mono<SubmissionDB> findById(UUID id);

  Mono<SubmissionDB> findById(UUID id, boolean operateOnMaster);

  Mono<SubmissionDB> findByTaskAndOwner(UUID taskId, UUID ownerId);

  Mono<Page<SubmissionDB>> findAll(SubmissionFilterOptions filterOptions, Pageable pageable);

  Mono<SubmissionDB> upsert(SubmissionDB submission);
}