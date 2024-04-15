package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UserProtoConverter;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.Success;
import ru.hse.lmsteam.backend.service.model.teams.ValidationErrors;
import ru.hse.lmsteam.schema.api.teams.*;

@Component
@RequiredArgsConstructor
public class TeamsApiProtoBuilderImpl implements TeamsApiProtoBuilder {
  private final TeamProtoConverter teamProtoConverter;
  private final TeamSnippetConverter teamSnippetConverter;
  private final UserProtoConverter userProtoConverter;

  @Override
  public GetTeam.Response buildGetTeamResponse(Team team) {
    var builder = GetTeam.Response.newBuilder();

    if (team != null) {
      builder.setTeam(teamProtoConverter.map(team));
    }
    return builder.build();
  }

  @Override
  public CreateOrUpdateTeam.Response buildCreateTeamResponse(Team team) {
    var builder = CreateOrUpdateTeam.Response.newBuilder();

    if (team != null) {
      builder.setTeam(teamProtoConverter.map(team));
    }
    return builder.build();
  }

  @Override
  public CreateOrUpdateTeam.Response buildUpdateTeamResponse(Team team) {
    var builder = CreateOrUpdateTeam.Response.newBuilder();

    if (team != null) {
      builder.setTeam(teamProtoConverter.map(team));
    }
    return builder.build();
  }

  @Override
  public DeleteTeam.Response buildDeleteTeamResponse(long itemsDeleted) {
    return DeleteTeam.Response.newBuilder().setEntitiesDeleted(itemsDeleted).build();
  }

  @Override
  public GetTeams.Response buildGetTeamsResponse(Page<Team> teams) {
    return GetTeams.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(teams.getTotalPages())
                .setTotalElements(teams.getTotalElements())
                .build())
        .addAllTeams(teams.stream().map(teamSnippetConverter::toSnippet).toList())
        .build();
  }

  @Override
  public GetTeamMembers.Response buildGetTeamMembersResponse(Collection<User> users) {
    return GetTeamMembers.Response.newBuilder()
        .addAllUsers(users.stream().map(userProtoConverter::map).toList())
        .build();
  }

  @Override
  public UpdateTeamMembers.Response buildUpdateTeamMembersResponse(
      SetUserTeamMembershipResponse r) {
    var builder = UpdateTeamMembers.Response.newBuilder();
    switch (r) {
      case Success():
        {
          builder.setSuccess(UpdateTeamMembers.Success.newBuilder().build());
          break;
        }
      case ValidationErrors(var usersNotFound, var alreadyMembers):
        {
          var errorsBuilder = UpdateTeamMembers.ValidationErrors.newBuilder();
          if (usersNotFound.isPresent()) {
            errorsBuilder.addAllNotFoundUserIds(
                usersNotFound.orElse(ImmutableSet.of()).stream().map(UUID::toString).toList());
          }

          alreadyMembers.ifPresent(
              alreadyMembersMap ->
                  errorsBuilder.addAllAlreadyMembers(
                      alreadyMembersMap.entrySet().stream()
                          .map(
                              entry -> {
                                var b = UpdateTeamMembers.ValidationErrors.UserTeams.newBuilder();
                                b.setUser(userProtoConverter.toSnippet(entry.getKey()));
                                b.addAllTeams(
                                    entry.getValue().stream()
                                        .map(teamSnippetConverter::toSnippet)
                                        .toList());
                                return b.build();
                              })
                          .toList()));
          builder.setErrors(errorsBuilder.build());
          break;
        }
      default:
        throw new IllegalStateException("Unexpected value: " + r);
    }
    return builder.build();
  }

  @Override
  public Team retrieveTeamModel(UUID id, CreateOrUpdateTeam.Request request) {
    return teamProtoConverter.retrieveUpdateModel(id, request);
  }
}
