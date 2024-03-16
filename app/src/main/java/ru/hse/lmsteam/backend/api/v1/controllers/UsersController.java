package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.UsersApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.UsersControllerDocSchema;
import ru.hse.lmsteam.backend.service.UserManagerImpl;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;
import ru.hse.lmsteam.schema.api.users.*;

@RestController
@RequestMapping(
    value = "/api/v1/users",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class UsersController implements UsersControllerDocSchema {
  private final UserManagerImpl usersManager;
  private final UsersApiProtoBuilder usersApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetUser.Response> getUser(@PathVariable UUID id) {
    return usersManager.findById(id).map(usersApiProtoBuilder::buildGetUserResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<UpdateOrCreateUser.Response> createUser(
      @RequestBody UpdateOrCreateUser.Request request) {
    var userUpsertModel = usersApiProtoBuilder.retrieveUserUpsertModel(request);
    return usersManager.create(userUpsertModel).map(usersApiProtoBuilder::buildUpdateUserResponse);
  }

  /**
   * If user entity provided without ID, or if there is no entity with provided ID method will fall
   * back to createUser logic.
   */
  @PatchMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<UpdateOrCreateUser.Response> updateUser(
      @RequestBody UpdateOrCreateUser.Request request) {
    var userUpsertModel = usersApiProtoBuilder.retrieveUserUpsertModel(request);
    if (userUpsertModel.id() == null) {
      throw new ValidationException("Id is null! Use POST /users to create entity.");
    }
    return usersManager.update(userUpsertModel).map(usersApiProtoBuilder::buildUpdateUserResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteUser.Response> deleteUser(@PathVariable UUID id) {
    return usersManager.delete(id).map(usersApiProtoBuilder::buildDeleteUserResponse);
  }

  // TODO protection over inconsistent properties and sql injections
  // also test groups module

  @GetMapping
  @Override
  @PageableAsQueryParam
  public Mono<GetUsers.Response> getUsers(
      @RequestParam(required = false) String namePattern,
      @RequestParam(required = false) String emailPattern,
      @RequestParam(required = false) List<Integer> groupNumbers,
      @RequestParam(required = false) List<String> roles,
      @RequestParam(required = false) Boolean isDeleted,
      Pageable pageable) {
    var filterOptions =
        UserFilterOptions.builder()
            .namePattern(namePattern)
            .emailPattern(emailPattern)
            .groupNumbers(ImmutableSet.copyOf(Optional.ofNullable(groupNumbers).orElse(List.of())))
            .roles(ImmutableSet.copyOf(Optional.ofNullable(roles).orElse(List.of())))
            .isDeleted(isDeleted)
            .build();
    return usersManager
        .findAll(filterOptions, pageable)
        .map(usersApiProtoBuilder::buildGetUsersResponse);
  }

  @GetMapping("/names")
  @Override
  public Mono<GetUserNameList.Response> getUserNameList() {
    return usersManager
        .getUserNamesList()
        .collect(ImmutableList.toImmutableList())
        .map(usersApiProtoBuilder::buildGetUserNameListResponse);
  }
}
