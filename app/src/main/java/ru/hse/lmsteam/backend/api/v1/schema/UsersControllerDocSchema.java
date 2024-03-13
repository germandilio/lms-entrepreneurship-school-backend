package ru.hse.lmsteam.backend.api.v1.schema;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.users.DeleteUser;
import ru.hse.lmsteam.schema.api.users.GetUser;
import ru.hse.lmsteam.schema.api.users.GetUserNameList;
import ru.hse.lmsteam.schema.api.users.GetUsers;
import ru.hse.lmsteam.schema.api.users.UpdateOrCreateUser;

public interface UsersControllerDocSchema {

  Mono<GetUser.Response> getUser(UUID id);

  Mono<UpdateOrCreateUser.Response> createUser(UpdateOrCreateUser.Request request);

  Mono<UpdateOrCreateUser.Response> updateUser(UpdateOrCreateUser.Request request);

  Mono<DeleteUser.Response> deleteUser(UUID id);

  Mono<GetUsers.Response> getUsers(
      String namePattern,
      String emailPattern,
      List<Integer> groupNumbers,
      List<String> roles,
      Boolean isDeleted,
      Pageable pageable);

  Mono<GetUserNameList.Response> getUserNameList();
}
