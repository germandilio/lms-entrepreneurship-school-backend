package ru.hse.lmsteam.backend.service.user;

import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.service.model.auth.AuthorizationResult;

public interface UserAuthInternal {
  Mono<? extends AuthorizationResult> tryRetrieveUser(String token);
}
