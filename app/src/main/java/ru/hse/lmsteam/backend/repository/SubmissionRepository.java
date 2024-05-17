package ru.hse.lmsteam.backend.repository;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.submission.SubmissionDB;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;

public interface SubmissionRepository {
  Mono<SubmissionDB> findById(UUID id);

  Mono<SubmissionDB> findById(UUID id, boolean operateOnMaster);

  Mono<SubmissionDB> findByTaskAndOwner(UUID taskId, UUID ownerId);

  Flux<SubmissionDB> findAllByIds(Collection<UUID> ids);

  Flux<SubmissionDB> findAllByTaskIds(Collection<UUID> taskId);

  Mono<Page<SubmissionDB>> findAll(SubmissionFilterOptions filterOptions, Pageable pageable);

  Mono<SubmissionDB> upsert(SubmissionDB submission);

  Flux<SubmissionDB> batchUpsert(Collection<SubmissionDB> submissions);

  Mono<Long> deleteAllGroupSubmissions(UUID taskId, UUID groupId);

  Mono<Long> deleteAllByTaskIds(Collection<UUID> taskIds);

  Flux<SubmissionDB> findAll();
}
