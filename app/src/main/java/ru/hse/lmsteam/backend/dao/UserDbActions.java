package ru.hse.lmsteam.backend.dao;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.model.User;

public interface UserDbActions {
  Mono<User> findById(String id);

  Flux<User> findAll(int limit, int offset);
}
