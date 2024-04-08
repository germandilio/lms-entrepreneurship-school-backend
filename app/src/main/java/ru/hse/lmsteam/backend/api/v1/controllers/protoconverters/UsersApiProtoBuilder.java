package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.user.UserSnippet;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.*;

public interface UsersApiProtoBuilder {
  GetUser.Response buildGetUserResponse(User user);

  GetUserBalance.Response buildGetUserBalanceResponse(BigDecimal balance);

  UpdateOrCreateUser.Response buildUpdateUserResponse(User user);

  DeleteUser.Response buildDeleteUserResponse(long itemsDeleted);

  GetUsers.Response buildGetUsersResponse(Page<User> users);

  GetUserNameList.Response buildGetUserNameListResponse(Collection<UserSnippet> names);

  UserUpsertModel retrieveUserUpsertModel(UUID userId, UpdateOrCreateUser.Request request);

  UserUpsertModel retrieveUserUpsertModel(UpdateOrCreateUser.Request request);
}
