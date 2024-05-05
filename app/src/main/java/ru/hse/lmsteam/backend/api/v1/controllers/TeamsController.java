package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams.TeamsApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.TeamsControllerDocSchema;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;
import ru.hse.lmsteam.backend.service.teams.TeamManager;
import ru.hse.lmsteam.schema.api.teams.*;

@RestController
@RequestMapping(
    value = "/api/v1/teams",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class TeamsController implements TeamsControllerDocSchema {
  private final TeamManager teamManager;
  private final TeamsApiProtoBuilder teamsApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetTeam.Response> getTeam(@PathVariable UUID id) {
    return teamManager
        .findById(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Team not found.")))
        .flatMap(t -> teamsApiProtoBuilder.buildGetTeamResponse(t, false));
  }

  @GetMapping("/{id}/public")
  @Override
  public Mono<GetTeam.Response> getTeamPublic(@PathVariable UUID id) {
    return teamManager
        .findById(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Team not found.")))
        .flatMap(t -> teamsApiProtoBuilder.buildGetTeamResponse(t, true));
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateTeam.Response> createTeam(
      @RequestBody CreateOrUpdateTeam.Request request) {
    var groupToCreate = teamsApiProtoBuilder.retrieveTeamModel(null, request);
    var userIds =
        request.getUserIdsList().stream()
            .map(UUID::fromString)
            .collect(ImmutableSet.toImmutableSet());
    return teamManager
        .create(groupToCreate, userIds)
        .flatMap(
            tuple ->
                teamsApiProtoBuilder.buildCreateOrUpdateTeamResponse(tuple.getT1(), tuple.getT2()));
  }

  @PatchMapping(
      path = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateTeam.Response> updateTeam(
      @PathVariable UUID id, @RequestBody CreateOrUpdateTeam.Request request) {
    var groupToUpdate = teamsApiProtoBuilder.retrieveTeamModel(id, request);
    if (groupToUpdate.id() == null) {
      throw new BusinessLogicExpectationFailedException(
          "Id is null! Use POST /groups to create entity.");
    }
    var userIds =
        request.getUserIdsList().stream()
            .map(UUID::fromString)
            .collect(ImmutableSet.toImmutableSet());

    return teamManager
        .update(groupToUpdate, userIds)
        .flatMap(
            tuple ->
                teamsApiProtoBuilder.buildCreateOrUpdateTeamResponse(tuple.getT1(), tuple.getT2()));
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteTeam.Response> deleteTeam(@PathVariable UUID id) {
    return teamManager
        .delete(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Team not found.")))
        .map(teamsApiProtoBuilder::buildDeleteTeamResponse);
  }

  @GetMapping("/list")
  @Override
  @PageableAsQueryParam
  public Mono<GetTeams.Response> getTeams(
      @RequestParam(required = false) Integer groupNumber, Pageable pageable) {
    var groupOptions = new TeamsFilterOptions(groupNumber);
    return teamManager
        .findAll(groupOptions, pageable)
        .map(teamsApiProtoBuilder::buildGetTeamsResponse);
  }

  @GetMapping("/{id}/members")
  @Override
  public Mono<GetTeamMembers.Response> getTeamMembers(@PathVariable UUID id) {
    return teamManager
        .getTeamMembers(id)
        .collectList()
        .map(teamsApiProtoBuilder::buildGetTeamMembersResponse);
  }

  @PutMapping("{id}/members")
  @Override
  public Mono<UpdateTeamMembers.Response> updateTeamMembers(
      @PathVariable UUID id, @RequestBody UpdateTeamMembers.Request request) {
    var userIds =
        request.getUserIdsList().stream()
            .map(UUID::fromString)
            .collect(ImmutableSet.toImmutableSet());
    return teamManager
        .updateTeamMembers(id, userIds)
        .map(teamsApiProtoBuilder::buildUpdateTeamMembersResponse);
  }

  @GetMapping("/{id}/members/validate")
  @Override
  public Mono<UpdateTeamMembers.Response> validateTeamMembers(
      @PathVariable UUID id, @RequestBody UpdateTeamMembers.Request request) {
    var userIds =
        request.getUserIdsList().stream()
            .map(UUID::fromString)
            .collect(ImmutableSet.toImmutableSet());
    return teamManager
        .validateTeamMembers(id, userIds)
        .map(teamsApiProtoBuilder::buildUpdateTeamMembersResponse);
  }
}
