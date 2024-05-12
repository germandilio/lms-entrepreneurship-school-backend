package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UsersApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.UsersControllerDocSchema;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;
import ru.hse.lmsteam.backend.service.user.UserManagerImpl;
import ru.hse.lmsteam.schema.api.users.*;

@RestController
@RequestMapping(
    value = "/api/v1/users",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class UsersController implements UsersControllerDocSchema {
  private final UserManagerImpl usersManager;
  private final UsersApiProtoBuilder usersApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetUser.Response> getUser(@PathVariable UUID id) {
    return usersManager
        .findById(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("User not found.")))
        .flatMap(usersApiProtoBuilder::buildGetUserResponse);
  }

  @GetMapping("/{id}/balance")
  @Override
  public Mono<GetUserBalance.Response> getUserBalance(@PathVariable UUID id) {
    return usersManager
        .getUserBalance(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("User not found.")))
        .map(usersApiProtoBuilder::buildGetUserBalanceResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateUser.Response> createUser(
      @RequestBody CreateOrUpdateUser.Request request) {
    var userUpsertModel = usersApiProtoBuilder.retrieveUserUpsertModel(request);
    return usersManager
        .create(userUpsertModel)
        .flatMap(usersApiProtoBuilder::buildCreateOrUpdateUserResponse);
  }

  /**
   * If user entity provided without ID, or if there is no entity with provided ID method will fall
   * back to createUser logic.
   */
  @PatchMapping(
      value = "/{userId}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateUser.Response> updateUser(
      @PathVariable UUID userId, @RequestBody CreateOrUpdateUser.Request request) {
    var userUpsertModel = usersApiProtoBuilder.retrieveUserUpsertModel(userId, request);
    return usersManager
        .update(userUpsertModel)
        .flatMap(usersApiProtoBuilder::buildCreateOrUpdateUserResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteUser.Response> deleteUser(@PathVariable UUID id) {
    return usersManager
        .delete(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("User not found.")))
        .map(usersApiProtoBuilder::buildDeleteUserResponse);
  }

  @GetMapping("/list")
  @Override
  public Mono<GetUsers.Response> getUsers(
      @RequestParam(required = false) String namePattern,
      @RequestParam(required = false) String emailPattern,
      @RequestParam(required = false) List<Integer> groupNumbers,
      @RequestParam(required = false) List<String> roles,
      @RequestParam(required = false) Boolean isDeleted,
      Pageable pageable) {
    var filterOptions =
        getUserFilterOptions(namePattern, emailPattern, groupNumbers, roles, isDeleted);
    return usersManager
        .findAll(filterOptions, pageable)
        .flatMap(usersApiProtoBuilder::buildGetUsersResponse);
  }

  @GetMapping("/snippets")
  @Override
  public Mono<GetUserNameList.Response> getUserSnippets(
      @RequestParam(required = false) String namePattern,
      @RequestParam(required = false) String emailPattern,
      @RequestParam(required = false) List<Integer> groupNumbers,
      @RequestParam(required = false) List<String> roles,
      @RequestParam(required = false) Boolean isDeleted,
      Pageable pageable) {
    var filterOptions =
        getUserFilterOptions(namePattern, emailPattern, groupNumbers, roles, isDeleted);
    return usersManager
        .findAll(filterOptions, pageable)
        .map(usersApiProtoBuilder::buildGetUserNameListResponse);
  }

  private UserFilterOptions getUserFilterOptions(
      String namePattern,
      String emailPattern,
      List<Integer> groupNumbers,
      List<String> roles,
      Boolean isDeleted) {
    return UserFilterOptions.builder()
        .namePattern(namePattern)
        .emailPattern(emailPattern)
        .groupNumbers(ImmutableSet.copyOf(Optional.ofNullable(groupNumbers).orElse(List.of())))
        .roles(
            Optional.ofNullable(roles).orElse(List.of()).stream()
                .map(String::toUpperCase)
                .filter(s -> !s.isBlank() && !"ADMIN".equals(s))
                .collect(ImmutableSet.toImmutableSet()))
        .isDeleted(isDeleted)
        .build();
  }
}
