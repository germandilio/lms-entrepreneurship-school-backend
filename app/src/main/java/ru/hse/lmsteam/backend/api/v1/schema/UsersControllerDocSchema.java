package ru.hse.lmsteam.backend.api.v1.schema;

import com.google.common.collect.ImmutableSet;
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

  Mono<UpdateOrCreateUser.Response> updateOrCreateUser(UpdateOrCreateUser.Request request);

  Mono<DeleteUser.Response> deleteUser(UUID id);

  Mono<GetUsers.Response> getUsers(
      String namePattern,
      String emailPattern,
      ImmutableSet<Integer> groupNumbers,
      ImmutableSet<String> roles,
      Boolean isDeleted,
      Pageable pageable);

  Mono<GetUserNameList.Response> getUserNameList();
}
