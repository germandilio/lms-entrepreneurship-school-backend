package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.groups.SetUserGroupMembershipResponse;
import ru.hse.lmsteam.schema.api.groups.*;

public interface GroupsApiProtoBuilder {
  GetGroup.Response buildGetGroupResponse(Group group);

  CreateOrUpdateGroup.Response buildCreateGroupResponse(Group group);

  CreateOrUpdateGroup.Response buildUpdateGroupResponse(Group group);

  DeleteGroup.Response buildDeleteGroupResponse(long itemsDeleted);

  GetGroups.Response buildGetGroupsResponse(Page<Group> groups);

  GetGroupMembers.Response buildGetGroupMembersResponse(Page<User> users);

  UpdateGroupMembers.Response buildUpdateGroupMembersResponse(
      SetUserGroupMembershipResponse response);

  Group retrieveGroupModel(CreateOrUpdateGroup.Request request);
}
