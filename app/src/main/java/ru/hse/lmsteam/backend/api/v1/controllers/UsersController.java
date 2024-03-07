package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.UsersApiProtoConverter;
import ru.hse.lmsteam.backend.api.v1.schema.UsersControllerDocSchema;
import ru.hse.lmsteam.backend.service.UserManagerImpl;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;
import ru.hse.lmsteam.schema.api.users.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersController implements UsersControllerDocSchema {
  private final UserManagerImpl usersManager;
  private final UsersApiProtoConverter usersApiProtoConverter;

  @GetMapping("/{id}")
  @Override
  public Mono<GetUser.Response> getUser(@PathVariable UUID id) {
    return usersManager.getUser(id).map(usersApiProtoConverter::buildGetUserResponse);
  }

  @PostMapping(
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<UpdateOrCreateUser.Response> updateOrCreateUser(
      @RequestBody UpdateOrCreateUser.Request request) {
    var user = usersApiProtoConverter.retrieveUser(request);
    return usersManager
        .updateOrCreateUser(user)
        .map(usersApiProtoConverter::buildUpdateUserResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteUser.Response> deleteUser(@PathVariable UUID id) {
    return usersManager.deleteUser(id).map(usersApiProtoConverter::buildDeleteUserResponse);
  }

  @GetMapping
  @Override
  public Mono<GetUsers.Response> getUsers(
      @RequestParam(required = false) String namePattern,
      @RequestParam(required = false) String emailPattern,
      @RequestParam(required = false) ImmutableSet<Integer> groupNumbers,
      @RequestParam(required = false) ImmutableSet<String> roles,
      @RequestParam(required = false) Boolean isDeleted,
      @RequestParam Pageable pageable) {
    var filterOptions =
        UserFilterOptions.builder()
            .namePattern(namePattern)
            .emailPattern(emailPattern)
            .groupNumbers(groupNumbers)
            .roles(roles)
            .isDeleted(isDeleted)
            .build();
    return usersManager
        .findUsers(filterOptions, pageable)
        .collect(ImmutableList.toImmutableList())
        .map(usersApiProtoConverter::buildGetUsersResponse);
  }

  @GetMapping("/names")
  @Override
  public Mono<GetUserNameList.Response> getUserNameList() {
    return usersManager
        .getUserNamesList()
        .collect(ImmutableList.toImmutableList())
        .map(usersApiProtoConverter::buildGetUserNameListResponse);
  }
}
