package ru.hse.lmsteam.backend.api.v1.controllers.tasks;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.CompetitionApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.tasks.CompetitionsControllerDocSchema;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.tasks.CompetitionFilterOptions;
import ru.hse.lmsteam.backend.service.tasks.CompetitionManager;
import ru.hse.lmsteam.schema.api.competitions.*;

@RestController
@RequestMapping(
    value = "/api/v1/competitions",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CompetitionsController implements CompetitionsControllerDocSchema {
  private final CompetitionManager competitionManager;
  private final CompetitionApiProtoBuilder competitionApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetCompetition.Response> getCompetition(@PathVariable UUID id) {
    return competitionManager
        .findById(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Competition not found.")))
        .map(competitionApiProtoBuilder::buildGetCompetitionResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateCompetition.Response> createCompetition(
      @RequestBody CreateOrUpdateCompetition.Request request) {
    var model = competitionApiProtoBuilder.retrieveCompetitionModel(request);

    return competitionManager
        .create(model)
        .map(competitionApiProtoBuilder::buildCreateOrUpdateCompetitionResponse);
  }

  @PutMapping(
      path = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateCompetition.Response> updateCompetition(
      @PathVariable UUID id, @RequestBody CreateOrUpdateCompetition.Request request) {
    var model = competitionApiProtoBuilder.retrieveCompetitionModel(request).withId(id);
    return competitionManager
        .update(model)
        .map(competitionApiProtoBuilder::buildCreateOrUpdateCompetitionResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteCompetition.Response> deleteCompetition(@PathVariable UUID id) {
    return competitionManager
        .delete(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Competition not found.")))
        .map(competitionApiProtoBuilder::buildDeleteCompetitionResponse);
  }

  @PageableAsQueryParam
  @GetMapping("/list")
  @Override
  public Mono<GetCompetitions.Response> getCompetitions(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Instant deadlineFrom,
      @RequestParam(required = false) Instant deadlineTo,
      @RequestParam(required = false) Instant publishFrom,
      @RequestParam(required = false) Instant publishTo,
      Pageable pageable) {
    var options =
        new CompetitionFilterOptions(title, publishFrom, publishTo, deadlineFrom, deadlineTo);
    return competitionManager
        .findAll(options, pageable)
        .map(competitionApiProtoBuilder::buildGetCompetitionsResponse);
  }
}
