package ru.hse.lmsteam.backend.api.v1.schema;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.users.*;

public interface UsersControllerDocSchema {

  Mono<GetUser.Response> getUser(UUID id);

  Mono<GetUserBalance.Response> getUserBalance(UUID id);

  Mono<CreateOrUpdateUser.Response> createUser(CreateOrUpdateUser.Request request);

  Mono<CreateOrUpdateUser.Response> updateUser(UUID userId, CreateOrUpdateUser.Request request);

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
