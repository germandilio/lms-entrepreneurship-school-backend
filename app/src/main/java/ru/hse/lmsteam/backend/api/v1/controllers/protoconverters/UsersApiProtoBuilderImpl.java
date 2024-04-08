package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.protobuf.StringValue;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.user.UserSnippet;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.*;

@RequiredArgsConstructor
@Component
public class UsersApiProtoBuilderImpl implements UsersApiProtoBuilder {
  private final UserProtoConverterImpl userProtoConverter;

  @Override
  public GetUser.Response buildGetUserResponse(User user) {
    var builder = GetUser.Response.newBuilder();
    if (user != null) {
      builder.setUser(userProtoConverter.map(user));
    }
    return builder.build();
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
  public UpdateOrCreateUser.Response buildUpdateUserResponse(User user) {
    var builder = UpdateOrCreateUser.Response.newBuilder();
    if (user != null) {
      builder.setUser(userProtoConverter.map(user));
    }
    return builder.build();
  }

  @Override
  public DeleteUser.Response buildDeleteUserResponse(long itemsDeleted) {
    return DeleteUser.Response.newBuilder().setEntitiesDeleted(itemsDeleted).build();
  }

  @Override
  public GetUsers.Response buildGetUsersResponse(Page<User> users) {
    return GetUsers.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(users.getTotalPages())
                .setTotalElements(users.getTotalElements())
                .build())
        .addAllUsers(users.stream().map(userProtoConverter::map).toList())
        .build();
  }

  @Override
  public GetUserNameList.Response buildGetUserNameListResponse(Collection<UserSnippet> items) {
    return GetUserNameList.Response.newBuilder()
        .addAllItems(
            items.stream()
                .map(
                    item -> {
                      var b =
                          ru.hse.lmsteam.schema.api.users.UserSnippet.newBuilder()
                              .setId(item.userId().toString())
                              .setName(item.name())
                              .setSurname(item.surname());
                      item.patronymic().ifPresent(p -> b.setPatronymic(StringValue.of(p)));
                      return b.build();
                    })
                .toList())
        .build();
  }

  @Override
  public UserUpsertModel retrieveUserUpsertModel(UUID userId, UpdateOrCreateUser.Request request) {
    var model = retrieveUserUpsertModel(request);
    return model.withId(userId);
  }

  @Override
  public UserUpsertModel retrieveUserUpsertModel(UpdateOrCreateUser.Request request) {
    return userProtoConverter.map(request);
  }
}
