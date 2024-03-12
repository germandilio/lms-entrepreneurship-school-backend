package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import java.util.Collection;
import ru.hse.lmsteam.backend.domain.user.Group;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.schema.api.groups.*;

public interface GroupsApiProtoBuilder {
  GetGroup.Response buildGetGroupResponse(Group group);

  CreateOrUpdateGroup.Response buildCreateGroupResponse(Group group);

  CreateOrUpdateGroup.Response buildUpdateGroupResponse(Group group);

  DeleteGroup.Response buildDeleteGroupResponse(long itemsDeleted);

  GetGroups.Response buildGetGroupsResponse(Collection<Group> groups);

  GetGroupMembers.Response buildGetGroupMembersResponse(Collection<User> users);

  UpdateGroupMembers.Response buildUpdateGroupMembersResponse(Collection<User> users);

  Group retrieveGroupModel(CreateOrUpdateGroup.Request request);
}
