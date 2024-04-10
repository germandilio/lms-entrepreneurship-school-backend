package ru.hse.lmsteam.backend.api.v1.schema;

import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.groups.*;

public interface GroupsControllerDocSchema {
  Mono<GetGroup.Response> getGroup(Integer id);

  Mono<CreateOrUpdateGroup.Response> createGroup(CreateOrUpdateGroup.Request request);

  Mono<CreateOrUpdateGroup.Response> updateGroup(CreateOrUpdateGroup.Request request);

  Mono<DeleteGroup.Response> deleteGroup(Integer id);

  Mono<GetGroups.Response> getGroups(Integer groupNumber, Pageable pageable);

  Mono<GetGroupMembers.Response> getGroupMembers(Integer id);

  Mono<UpdateGroupMembers.Response> updateGroupMembers(
      Integer groupId, UpdateGroupMembers.Request request);

  Mono<UpdateGroupMembers.Response> validateGroupMembers(
      Integer id, UpdateGroupMembers.Request request);
}
