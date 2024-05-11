package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import java.util.UUID;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.schema.api.teams.CreateOrUpdateTeam;

public interface TeamProtoConverter {
  Mono<ru.hse.lmsteam.schema.api.teams.Team> map(Team team);

  Mono<ru.hse.lmsteam.schema.api.teams.Team> map(Team team, boolean forPublicUser);

  Team retrieveUpdateModel(UUID id, CreateOrUpdateTeam.Request request);
}
