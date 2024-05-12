package ru.hse.lmsteam.backend.service.tasks;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.backend.service.model.tasks.CompetitionFilterOptions;

public interface CompetitionManager {
  Mono<Competition> findById(UUID id);

  Mono<Map<UUID, Competition>> findByIds(Collection<UUID> ids);

  Mono<Competition> create(Competition assignment);

  Mono<Competition> update(Competition assignment);

  Mono<Long> delete(UUID assignmentId);

  Mono<Page<Competition>> findAll(CompetitionFilterOptions filterOptions, Pageable pageable);
}
