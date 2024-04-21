package ru.hse.lmsteam.backend.api.v1.schema.tasks;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.competitions.CreateOrUpdateCompetition;
import ru.hse.lmsteam.schema.api.competitions.DeleteCompetition;
import ru.hse.lmsteam.schema.api.competitions.GetCompetition;
import ru.hse.lmsteam.schema.api.competitions.GetCompetitions;

public interface CompetitionsControllerDocSchema {
  Mono<GetCompetition.Response> getCompetition(UUID id);

  Mono<CreateOrUpdateCompetition.Response> createCompetition(
      CreateOrUpdateCompetition.Request request);

  Mono<CreateOrUpdateCompetition.Response> updateCompetition(
      UUID id, CreateOrUpdateCompetition.Request request);

  Mono<DeleteCompetition.Response> deleteCompetition(UUID id);

  Mono<GetCompetitions.Response> getCompetitions(
      String title,
      Instant deadlineFrom,
      Instant deadlineTo,
      Instant publishFrom,
      Instant publishTo,
      Pageable pageable);
}
