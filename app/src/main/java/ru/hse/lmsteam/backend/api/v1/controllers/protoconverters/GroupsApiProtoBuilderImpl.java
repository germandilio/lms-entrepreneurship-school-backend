package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;
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
  public UpdateGroupMembers.Response buildUpdateGroupMembersResponse(Collection<User> users) {
    return UpdateGroupMembers.Response.newBuilder()
        .addAllUsers(users.stream().map(userProtoConverter::map).toList())
        .build();
  }

  @Override
  public Group retrieveGroupModel(CreateOrUpdateGroup.Request request) {
    return groupProtoConverter.retrieveUpdateModel(request);
  }
}
