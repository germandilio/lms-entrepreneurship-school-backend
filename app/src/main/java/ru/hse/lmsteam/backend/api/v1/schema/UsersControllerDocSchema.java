package ru.hse.lmsteam.backend.api.v1.schema;

import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.users.GetAllUsers;
import ru.hse.lmsteam.schema.api.users.GetUserById;

public interface UsersControllerDocSchema {
  Mono<GetAllUsers.Response> getAllUsers(GetAllUsers.Request request);

  Mono<GetUserById.Response> getUserById(GetUserById.Request request);
}
