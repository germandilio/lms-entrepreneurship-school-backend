package ru.hse.lmsteam.backend.service;

import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.service.model.AuthorizationResult;

public interface AuthorizationManager {
  Mono<? extends AuthorizationResult> authorize(String token);
}
