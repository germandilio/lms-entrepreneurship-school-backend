package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user;

import ru.hse.lmsteam.backend.service.model.auth.AuthResult;
import ru.hse.lmsteam.schema.api.users.auth.*;

public interface UserAuthProtoConverter {
  ru.hse.lmsteam.schema.api.users.auth.AuthResponse toProto(AuthResult model);
}
