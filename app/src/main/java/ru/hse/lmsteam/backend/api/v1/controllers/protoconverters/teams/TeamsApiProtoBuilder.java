package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.schema.api.teams.*;

public interface TeamsApiProtoBuilder {
  Mono<GetTeam.Response> buildGetTeamResponse(Team team, boolean forPublicUser);

  Mono<CreateOrUpdateTeam.Response> buildCreateOrUpdateTeamResponse(
      Team team, SetUserTeamMembershipResponse setTeamMembershipResponse);

  DeleteTeam.Response buildDeleteTeamResponse(long itemsDeleted);

  GetTeams.Response buildGetTeamsResponse(Page<Team> Teams);

  Mono<GetTeamMembers.Response> buildGetTeamMembersResponse(Collection<User> users);

  UpdateTeamMembers.Response buildUpdateTeamMembersResponse(SetUserTeamMembershipResponse response);

  Team retrieveTeamModel(UUID id, CreateOrUpdateTeam.Request request);
}
