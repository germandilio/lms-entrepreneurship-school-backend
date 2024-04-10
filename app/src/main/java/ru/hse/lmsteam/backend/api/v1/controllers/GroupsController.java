package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.common.collect.ImmutableSet;
import jakarta.validation.ValidationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.GroupsApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.GroupsControllerDocSchema;
import ru.hse.lmsteam.backend.service.GroupManager;
import ru.hse.lmsteam.backend.service.model.groups.GroupsFilterOptions;
import ru.hse.lmsteam.schema.api.groups.*;

@RestController
@RequestMapping(
    value = "/api/v1/groups",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class GroupsController implements GroupsControllerDocSchema {
  private final GroupManager groupManager;
  private final GroupsApiProtoBuilder groupsApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetGroup.Response> getGroup(@PathVariable Integer id) {
    return groupManager.findById(id).map(groupsApiProtoBuilder::buildGetGroupResponse);
  }

  @PostMapping
  @Override
  public Mono<CreateOrUpdateGroup.Response> createGroup(
      @RequestBody CreateOrUpdateGroup.Request request) {
    var groupToCreate = groupsApiProtoBuilder.retrieveGroupModel(request);
    return groupManager.create(groupToCreate).map(groupsApiProtoBuilder::buildCreateGroupResponse);
  }

  @PatchMapping
  @Override
  public Mono<CreateOrUpdateGroup.Response> updateGroup(
      @RequestBody CreateOrUpdateGroup.Request request) {
    var groupToUpdate = groupsApiProtoBuilder.retrieveGroupModel(request);
    if (groupToUpdate.id() == null) {
      throw new ValidationException("Id is null! Use POST /groups to create entity.");
    }
    return groupManager.update(groupToUpdate).map(groupsApiProtoBuilder::buildUpdateGroupResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteGroup.Response> deleteGroup(@PathVariable Integer id) {
    return groupManager.delete(id).map(groupsApiProtoBuilder::buildDeleteGroupResponse);
  }

  @GetMapping
  @Override
  @PageableAsQueryParam
  public Mono<GetGroups.Response> getGroups(
      @RequestParam(required = false) Integer groupNumber, Pageable pageable) {
    var groupOptions = new GroupsFilterOptions(groupNumber);
    return groupManager
        .findAll(groupOptions, pageable)
        .map(groupsApiProtoBuilder::buildGetGroupsResponse);
  }

  @GetMapping("/{id}/members")
  @Override
  public Mono<GetGroupMembers.Response> getGroupMembers(@PathVariable Integer id) {
    return groupManager
        .getGroupMembers(id)
        .collectList()
        .map(groupsApiProtoBuilder::buildGetGroupMembersResponse);
  }

  @PutMapping("{id}/members")
  @Override
  public Mono<UpdateGroupMembers.Response> updateGroupMembers(
      @PathVariable Integer id, @RequestBody UpdateGroupMembers.Request request) {
    var userIds =
        request.getUserIdsList().stream()
            .map(UUID::fromString)
            .collect(ImmutableSet.toImmutableSet());
    return groupManager
        .updateGroupMembers(id, userIds)
        .map(groupsApiProtoBuilder::buildUpdateGroupMembersResponse);
  }

  @GetMapping("/{id}/members/validate")
  @Override
  public Mono<UpdateGroupMembers.Response> validateGroupMembers(
      @PathVariable Integer id, @RequestBody UpdateGroupMembers.Request request) {
    var userIds =
        request.getUserIdsList().stream()
            .map(UUID::fromString)
            .collect(ImmutableSet.toImmutableSet());
    return groupManager
        .validateGroupMembers(id, userIds)
        .map(groupsApiProtoBuilder::buildUpdateGroupMembersResponse);
  }
}
