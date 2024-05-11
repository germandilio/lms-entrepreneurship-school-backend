package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user;

import com.google.protobuf.StringValue;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.service.model.auth.AuthResult;
import ru.hse.lmsteam.schema.api.users.UserRoleNamespace;
import ru.hse.lmsteam.schema.api.users.auth.AuthResponse;
import ru.hse.lmsteam.schema.api.users.auth.Failure;
import ru.hse.lmsteam.schema.api.users.auth.Success;

@Component
public class UserAuthProtoConverterImpl implements UserAuthProtoConverter {
  @Override
  public AuthResponse toProto(AuthResult model) {
    var builder = AuthResponse.newBuilder();
    if (model.success()) {
      var user = model.user().get();
      var successBuilder =
          Success.newBuilder()
              .setUserId(user.id().toString())
              .setName(user.name())
              .setSurname(user.surname())
              .setRole(convertUserRole(model.role().get()))
              .setToken(model.authToken().get());
      if (user.patronymic() != null) {
        successBuilder.setPatronymic(StringValue.of(user.patronymic()));
      }

      builder.setSuccess(successBuilder.build());
    } else {
      builder.setFailure(Failure.newBuilder().setMessage(model.message()).build());
    }

    return builder.build();
  }

  private UserRoleNamespace.Role convertUserRole(UserRole role) {
    if (role == null) return null;
    return switch (role) {
      case ADMIN -> UserRoleNamespace.Role.ADMIN;
      case LEARNER -> UserRoleNamespace.Role.LEARNER;
      case TRACKER -> UserRoleNamespace.Role.TRACKER;
    };
  }
}
