package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.groups.SetUserGroupMembershipResponse;
import ru.hse.lmsteam.backend.service.model.groups.Success;
import ru.hse.lmsteam.backend.service.model.groups.ValidationErrors;
import ru.hse.lmsteam.schema.api.groups.*;

@Component
@RequiredArgsConstructor
public class GroupsApiProtoBuilderImpl implements GroupsApiProtoBuilder {
  private final GroupProtoConverter groupProtoConverter;
  private final UserProtoConverter userProtoConverter;

  @Override
  public GetGroup.Response buildGetGroupResponse(Group group) {
    var builder = GetGroup.Response.newBuilder();

    if (group != null) {
      builder.setGroup(groupProtoConverter.map(group));
    }
    return builder.build();
  }

  @Override
  public CreateOrUpdateGroup.Response buildCreateGroupResponse(Group group) {
    var builder = CreateOrUpdateGroup.Response.newBuilder();

    if (group != null) {
      builder.setGroup(groupProtoConverter.map(group));
    }
    return builder.build();
  }

  @Override
  public CreateOrUpdateGroup.Response buildUpdateGroupResponse(Group group) {
    var builder = CreateOrUpdateGroup.Response.newBuilder();

    if (group != null) {
      builder.setGroup(groupProtoConverter.map(group));
    }
    return builder.build();
  }

  @Override
  public DeleteGroup.Response buildDeleteGroupResponse(long itemsDeleted) {
    return DeleteGroup.Response.newBuilder().setEntitiesDeleted(itemsDeleted).build();
  }

  @Override
  public GetGroups.Response buildGetGroupsResponse(Page<Group> groups) {
    return GetGroups.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(groups.getTotalPages())
                .setTotalElements(groups.getTotalElements())
                .build())
        .addAllGroups(groups.stream().map(groupProtoConverter::map).toList())
        .build();
  }

  @Override
  public GetGroupMembers.Response buildGetGroupMembersResponse(Page<User> users) {
    return GetGroupMembers.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(users.getTotalPages())
                .setTotalElements(users.getTotalElements())
                .build())
        .addAllUsers(users.stream().map(userProtoConverter::map).toList())
        .build();
  }

  @Override
  public UpdateGroupMembers.Response buildUpdateGroupMembersResponse(
      SetUserGroupMembershipResponse r) {
    var builder = UpdateGroupMembers.Response.newBuilder();
    switch (r) {
      case Success():
        {
          builder.setSuccess(UpdateGroupMembers.Success.newBuilder().build());
          break;
        }
      case ValidationErrors(var usersNotFound, var alreadyMembers):
        {
          var errorsBuilder = UpdateGroupMembers.ValidationErrors.newBuilder();
          if (usersNotFound.isPresent()) {
            errorsBuilder.addAllNotFoundUserIds(
                usersNotFound.orElse(ImmutableSet.of()).stream().map(UUID::toString).toList());
          }
          if (alreadyMembers.isPresent()) {
            errorsBuilder.putAllAlreadyMembers(
                alreadyMembers.orElse(ImmutableMap.of()).entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            el -> el.getKey().toString(),
                            el -> groupProtoConverter.toSnippet(el.getValue()))));
          }
          builder.setErrors(errorsBuilder.build());
          break;
        }
      default:
        throw new IllegalStateException("Unexpected value: " + r);
    }
    return builder.build();
  }

  @Override
  public Group retrieveGroupModel(CreateOrUpdateGroup.Request request) {
    return groupProtoConverter.retrieveUpdateModel(request);
  }
}
