package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.user.UserSnippet;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.*;

public interface UsersApiProtoBuilder {
  Mono<GetUser.Response> buildGetUserResponse(User user);

  GetUserBalance.Response buildGetUserBalanceResponse(BigDecimal balance);

  Mono<CreateOrUpdateUser.Response> buildCreateOrUpdateUserResponse(User user);

  DeleteUser.Response buildDeleteUserResponse(long itemsDeleted);

  Mono<GetUsers.Response> buildGetUsersResponse(Page<User> users);

  GetUserNameList.Response buildGetUserNameListResponse(Collection<UserSnippet> names);

  UserUpsertModel retrieveUserUpsertModel(UUID userId, CreateOrUpdateUser.Request request);

  UserUpsertModel retrieveUserUpsertModel(CreateOrUpdateUser.Request request);
}
