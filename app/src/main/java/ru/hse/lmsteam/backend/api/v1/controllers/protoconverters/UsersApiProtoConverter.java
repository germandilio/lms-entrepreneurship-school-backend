package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import java.util.Collection;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.*;

public interface UsersApiProtoConverter {
  GetUser.Response buildGetUserResponse(User user);

  UpdateOrCreateUser.Response buildUpdateUserResponse(User user);

  DeleteUser.Response buildDeleteUserResponse(long itemsDeleted);

  GetUsers.Response buildGetUsersResponse(Collection<User> users);

  GetUserNameList.Response buildGetUserNameListResponse(Collection<String> names);

  UserUpsertModel retrieveUserUpsertModel(UpdateOrCreateUser.Request request);
}
