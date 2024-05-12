package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.backend.service.teams.TeamManager;
import ru.hse.lmsteam.backend.service.teams.UserTeamManager;
import ru.hse.lmsteam.schema.api.users.*;

@RequiredArgsConstructor
@Component
public class UsersApiProtoBuilderImpl implements UsersApiProtoBuilder {
  private final UserTeamManager userTeamManager;
  private final TeamManager teamManager;
  private final UserProtoConverter userProtoConverter;

  @Override
  public Mono<GetUser.Response> buildGetUserResponse(User user) {
    return teamManager
        .findByMember(user.id())
        .collectList()
        .map(
            userTeams -> {
              var builder = GetUser.Response.newBuilder();
              builder.setUser(userProtoConverter.map(user, userTeams));
              return builder.build();
            });
  }

  @Override
  public GetUserBalance.Response buildGetUserBalanceResponse(BigDecimal balance) {
    var builder = GetUserBalance.Response.newBuilder();
    if (balance != null) {
      builder.setBalance(balance.toString());
    }
    return builder.build();
  }

  @Override
  public Mono<CreateOrUpdateUser.Response> buildCreateOrUpdateUserResponse(User user) {
    return teamManager
        .findByMember(user.id())
        .collectList()
        .map(
            userTeams -> {
              var builder = CreateOrUpdateUser.Response.newBuilder();
              builder.setUser(userProtoConverter.map(user, userTeams));
              return builder.build();
            });
  }

  @Override
  public DeleteUser.Response buildDeleteUserResponse(long itemsDeleted) {
    return DeleteUser.Response.newBuilder().setEntitiesDeleted(itemsDeleted).build();
  }

  @Override
  public Mono<GetUsers.Response> buildGetUsersResponse(Page<User> users) {
    return userTeamManager
        .getUserGroups(users.getContent())
        .map(
            userGroups -> {
              var page =
                  ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                      .setTotalPages(users.getTotalPages())
                      .setTotalElements(users.getTotalElements())
                      .build();

              var protoUsers =
                  users.getContent().stream()
                      .map(u -> userProtoConverter.map(u, userGroups.get(u)))
                      .toList();
              return GetUsers.Response.newBuilder().setPage(page).addAllUsers(protoUsers).build();
            });
  }

  @Override
  public GetUserNameList.Response buildGetUserNameListResponse(Page<User> items) {
    return GetUserNameList.Response.newBuilder()
        .addAllItems(items.stream().map(userProtoConverter::toSnippet).toList())
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(items.getTotalPages())
                .setTotalElements(items.getTotalElements())
                .build())
        .build();
  }

  @Override
  public UserUpsertModel retrieveUserUpsertModel(UUID userId, CreateOrUpdateUser.Request request) {
    var model = retrieveUserUpsertModel(request);
    return model.withId(userId);
  }

  @Override
  public UserUpsertModel retrieveUserUpsertModel(CreateOrUpdateUser.Request request) {
    return userProtoConverter.map(request);
  }
}
