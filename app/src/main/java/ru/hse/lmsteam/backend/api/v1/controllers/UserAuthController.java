package ru.hse.lmsteam.backend.api.v1.controllers;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.UserAuthProtoConverter;
import ru.hse.lmsteam.backend.service.UserAuthManager;
import ru.hse.lmsteam.schema.api.users.auth.AuthResponse;

@RestController
@RequestMapping(
    value = "/api/v1/auth",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class UserAuthController {
  private final UserAuthManager userAuthManager;
  private final UserAuthProtoConverter userAuthProtoConverter;

  @GetMapping("/login/")
  public Mono<AuthResponse> login(
      @RequestParam @NotEmpty String login, @RequestParam @NotEmpty String password) {
    return userAuthManager.authenticate(login, password).map(userAuthProtoConverter::toProto);
  }
}
