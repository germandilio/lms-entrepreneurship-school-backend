package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import ru.hse.lmsteam.backend.domain.user.User;

public interface UserProtoConverter {
  ru.hse.lmsteam.schema.api.users.User map(User user);

  User map(ru.hse.lmsteam.schema.api.users.User user);
}
