package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.UpdateOrCreateUser;

public interface UserProtoConverter {
  ru.hse.lmsteam.schema.api.users.User map(User user);

  ru.hse.lmsteam.schema.api.users.UserSnippet toSnippet(User user);

  User map(ru.hse.lmsteam.schema.api.users.User user);

  UserUpsertModel map(UpdateOrCreateUser.Request request);
}
