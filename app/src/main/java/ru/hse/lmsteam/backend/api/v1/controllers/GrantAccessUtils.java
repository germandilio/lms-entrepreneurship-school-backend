package ru.hse.lmsteam.backend.api.v1.controllers;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.UserAuth;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicAccessDeniedException;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicUnauthorizedException;
import ru.hse.lmsteam.backend.service.model.auth.InternalAuthorizationResult;
import ru.hse.lmsteam.backend.service.user.UserAuthInternal;

@Component
@RequiredArgsConstructor
public class GrantAccessUtils {
  private final UserAuthInternal authorizationManager;

  public Mono<UserAuth> grantAccess(String rawToken, Set<UserRole> allowedRoles) {
    var token = rawToken.startsWith("Bearer ") ? rawToken.substring(7) : rawToken;
    return authorizationManager
        .tryRetrieveUser(token)
        .flatMap(
            authResult -> {
              if (Objects.requireNonNull(authResult)
                  instanceof InternalAuthorizationResult(UserAuth user)) {
                if (!allowedRoles.contains(user.role())) {
                  return Mono.error(
                      new BusinessLogicAccessDeniedException(
                          "User is not allowed to create submission."));
                }

                return Mono.just(user);
              }
              return Mono.error(
                  new BusinessLogicUnauthorizedException(
                      "Failed to authorize user by given token."));
            });
  }
}
