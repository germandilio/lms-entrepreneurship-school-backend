package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.schema.api.teams.*;

public interface TeamsApiProtoBuilder {
  GetTeam.Response buildGetTeamResponse(Team team);

  CreateOrUpdateTeam.Response buildCreateTeamResponse(Team team);

  CreateOrUpdateTeam.Response buildUpdateTeamResponse(Team team);

  DeleteTeam.Response buildDeleteTeamResponse(long itemsDeleted);

  GetTeams.Response buildGetTeamsResponse(Page<Team> Teams);

  GetTeamMembers.Response buildGetTeamMembersResponse(Collection<User> users);

  UpdateTeamMembers.Response buildUpdateTeamMembersResponse(SetUserTeamMembershipResponse response);

  Team retrieveTeamModel(UUID id, CreateOrUpdateTeam.Request request);
}
