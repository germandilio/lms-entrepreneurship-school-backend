package ru.hse.lmsteam.backend.api.v1.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.schema.UsersControllerDocSchema;
import ru.hse.lmsteam.backend.managers.UsersManager;
import ru.hse.lmsteam.schema.api.users.GetAllUsers;
import ru.hse.lmsteam.schema.api.users.GetUserById;

@RestController
@RequestMapping(
    path = "/api/v1/users",
    consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE},
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class UsersController implements UsersControllerDocSchema {
  private final UsersManager usersManager;

  @PostMapping("/")
  @Override
  public Mono<GetAllUsers.Response> getAllUsers(@RequestBody GetAllUsers.Request request) {
    return usersManager.getAllUsers(request);
  }

  @PostMapping("/by-id")
  @Override
  public Mono<GetUserById.Response> getUserById(@RequestBody GetUserById.Request request) {
    return usersManager.getUserById(request);
  }
}
