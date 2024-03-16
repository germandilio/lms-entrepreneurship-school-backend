package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import java.util.Collection;
import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.*;

public interface UsersApiProtoBuilder {
  GetUser.Response buildGetUserResponse(User user);

  UpdateOrCreateUser.Response buildUpdateUserResponse(User user);

  DeleteUser.Response buildDeleteUserResponse(long itemsDeleted);

  GetUsers.Response buildGetUsersResponse(Page<User> users);

  GetUserNameList.Response buildGetUserNameListResponse(Collection<String> names);

  UserUpsertModel retrieveUserUpsertModel(UpdateOrCreateUser.Request request);
}
