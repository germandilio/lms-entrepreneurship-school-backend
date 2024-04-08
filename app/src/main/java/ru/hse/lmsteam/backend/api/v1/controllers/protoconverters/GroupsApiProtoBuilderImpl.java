package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.UUID;
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
  private final GroupSnippetConverter groupSnippetConverter;
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
  public GetGroupMembers.Response buildGetGroupMembersResponse(Collection<User> users) {
    return GetGroupMembers.Response.newBuilder()
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

          alreadyMembers.ifPresent(
              alreadyMembersMap ->
                  errorsBuilder.addAllAlreadyMembers(
                      alreadyMembersMap.entrySet().stream()
                          .map(
                              entry -> {
                                var b = UpdateGroupMembers.ValidationErrors.UserGroups.newBuilder();
                                b.setUser(userProtoConverter.toSnippet(entry.getKey()));
                                b.addAllGroups(
                                    entry.getValue().stream()
                                        .map(groupSnippetConverter::toSnippet)
                                        .toList());
                                return b.build();
                              })
                          .toList()));
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
