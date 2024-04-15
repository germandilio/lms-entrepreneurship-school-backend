package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import java.util.UUID;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.schema.api.teams.CreateOrUpdateTeam;

public interface TeamProtoConverter {
  ru.hse.lmsteam.schema.api.teams.Team map(Team team);

  Team retrieveUpdateModel(UUID id, CreateOrUpdateTeam.Request request);
}
