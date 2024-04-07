package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.auth.AuthResult;
import ru.hse.lmsteam.schema.api.users.auth.AuthResponse;
import ru.hse.lmsteam.schema.api.users.auth.Failure;
import ru.hse.lmsteam.schema.api.users.auth.Success;

@Component
public class UserAuthProtoConverterImpl implements UserAuthProtoConverter {
  @Override
  public AuthResponse toProto(AuthResult model) {
    var builder = AuthResponse.newBuilder();
    if (model.success()) {
      builder.setSuccess(
          Success.newBuilder()
              .setUserId(model.userId().get().toString())
              .setRole(model.role().get().name())
              .setToken(model.authToken().get())
              .build());
    } else {
      builder.setFailure(Failure.newBuilder().setMessage(model.message()).build());
    }

    return builder.build();
  }
}
