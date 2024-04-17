package ru.hse.lmsteam.backend.api.v1.schema;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.teams.*;

public interface TeamsControllerDocSchema {
  Mono<GetTeam.Response> getTeam(UUID id);

  Mono<GetTeam.Response> getTeamPublic(UUID id);

  Mono<CreateOrUpdateTeam.Response> createTeam(CreateOrUpdateTeam.Request request);

  Mono<CreateOrUpdateTeam.Response> updateTeam(UUID id, CreateOrUpdateTeam.Request request);

  Mono<DeleteTeam.Response> deleteTeam(UUID id);

  Mono<GetTeams.Response> getTeams(Integer groupNumber, Pageable pageable);

  Mono<GetTeamMembers.Response> getTeamMembers(UUID id);

  Mono<UpdateTeamMembers.Response> updateTeamMembers(
      UUID groupId, UpdateTeamMembers.Request request);

  Mono<UpdateTeamMembers.Response> validateTeamMembers(UUID id, UpdateTeamMembers.Request request);
}
