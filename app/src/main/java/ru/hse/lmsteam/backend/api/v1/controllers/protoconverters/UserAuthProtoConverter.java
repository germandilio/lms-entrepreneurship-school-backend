package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import ru.hse.lmsteam.schema.api.users.auth.*;

public interface UserAuthProtoConverter {
  ru.hse.lmsteam.schema.api.users.auth.AuthResponse toProto(
      ru.hse.lmsteam.backend.service.model.AuthResult model);
}
