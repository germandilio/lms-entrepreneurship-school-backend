package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.UserAuth;

public interface UserAuthRepository {
  Mono<UserAuth> findByLogin(String login, boolean operateOnMaster);

  Mono<UserAuth> findByLogin(String login, boolean operateOnMaster, boolean shouldIncludeDeleted);

  Mono<UserAuth> findById(UUID userId, boolean operateOnMaster);

  Mono<UserAuth> upsert(UserAuth userAuth);

  Mono<Long> delete(UUID userId);
}
