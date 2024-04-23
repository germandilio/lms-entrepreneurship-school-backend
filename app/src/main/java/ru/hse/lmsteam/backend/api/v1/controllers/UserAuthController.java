package ru.hse.lmsteam.backend.api.v1.controllers;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UserAuthProtoConverter;
import ru.hse.lmsteam.backend.service.model.auth.AuthorizationResult;
import ru.hse.lmsteam.backend.service.user.UserAuthManager;
import ru.hse.lmsteam.schema.api.users.auth.ChangePassword;
import ru.hse.lmsteam.schema.api.users.auth.Login;
import ru.hse.lmsteam.schema.api.users.auth.SetPassword;

@RestController
@RequestMapping(
    value = "/api/v1/auth",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class UserAuthController {
  private final UserAuthManager userAuthManager;
  private final UserAuthProtoConverter userAuthProtoConverter;

  @PostMapping("/login")
  public Mono<Login.Response> login(@RequestParam String login, @RequestParam String password) {
    return userAuthManager
        .authenticate(login, password)
        .map(
            auth ->
                Login.Response.newBuilder()
                    .setResponse(userAuthProtoConverter.toProto(auth))
                    .build());
  }

  @PostMapping(
      path = "/change-password",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  public Mono<ChangePassword.Response> changePassword(@RequestBody ChangePassword.Request request) {
    return userAuthManager
        .changePassword(request.getLogin(), request.getOldPassword(), request.getNewPassword())
        .map(
            auth ->
                ChangePassword.Response.newBuilder()
                    .setResponse(userAuthProtoConverter.toProto(auth))
                    .build());
  }

  @PostMapping(
      path = "/set-password",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  public Mono<SetPassword.Response> setPassword(@RequestBody SetPassword.Request request) {
    return userAuthManager
        .setPassword(
            request.getLogin(), UUID.fromString(request.getToken()), request.getNewPassword())
        .map(
            auth ->
                SetPassword.Response.newBuilder()
                    .setResponse(userAuthProtoConverter.toProto(auth))
                    .build());
  }

  @GetMapping("/check-token")
  public Mono<Boolean> checkToken(@RequestParam String token) {
    return userAuthManager.authorize(token).map(AuthorizationResult::isAuthorized);
  }
}
