package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.schema.api.users.*;

@RequiredArgsConstructor
@Component
public class UsersApiProtoConverterImpl implements UsersApiProtoConverter {
  private final UserProtoConverterImpl userProtoConverter;

  @Override
  public GetUser.Response buildGetUserResponse(User user) {
    return GetUser.Response.newBuilder().setUser(userProtoConverter.map(user)).build();
  }

  @Override
  public UpdateOrCreateUser.Response buildUpdateUserResponse(User user) {
    return UpdateOrCreateUser.Response.newBuilder().setUser(userProtoConverter.map(user)).build();
  }

  @Override
  public DeleteUser.Response buildDeleteUserResponse(long itemsDeleted) {
    return DeleteUser.Response.newBuilder().setEntitiesDeleted(itemsDeleted).build();
  }

  @Override
  public GetUsers.Response buildGetUsersResponse(Collection<User> users) {
    return GetUsers.Response.newBuilder()
        .addAllUsers(users.stream().map(userProtoConverter::map).toList())
        .build();
  }

  @Override
  public GetUserNameList.Response buildGetUserNameListResponse(Collection<String> names) {
    return GetUserNameList.Response.newBuilder().addAllUserNames(names).build();
  }

  @Override
  public User retrieveUser(UpdateOrCreateUser.Request request) {
    return userProtoConverter.map(request.getUser());
  }
}
