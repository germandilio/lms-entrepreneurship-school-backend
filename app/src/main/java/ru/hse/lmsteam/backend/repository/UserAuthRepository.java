package ru.hse.lmsteam.backend.repository;

import java.util.UUID;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;

public interface UserAuthRepository {
  Mono<UserAuth> findByLogin(String login, boolean operateOnMaster);

  Mono<UserAuth> findByLogin(String login, boolean operateOnMaster, boolean shouldIncludeDeleted);

  Mono<UserAuth> findByUserId(UUID userId, boolean operateOnMaster);

  Mono<UserAuth> findByToken(UUID token);

  Mono<UserAuth> insert(UserAuth userAuth);

  Mono<UserAuth> update(UserAuth userAuth);

  Mono<Long> delete(UUID userId);
}
