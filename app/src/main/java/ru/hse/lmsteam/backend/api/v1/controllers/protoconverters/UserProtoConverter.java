package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.UpdateOrCreateUser;

public interface UserProtoConverter {
  ru.hse.lmsteam.schema.api.users.User map(User user);

  User map(ru.hse.lmsteam.schema.api.users.User user);

  UserUpsertModel map(UpdateOrCreateUser.Request request);
}
