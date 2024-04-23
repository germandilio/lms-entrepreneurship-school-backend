package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UserProtoConverter;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.teams.Success;
import ru.hse.lmsteam.schema.api.teams.*;

@Component
@RequiredArgsConstructor
public class TeamsApiProtoBuilderImpl implements TeamsApiProtoBuilder {
  private final TeamProtoConverter teamProtoConverter;
  private final TeamSnippetConverter teamSnippetConverter;
  private final UserProtoConverter userProtoConverter;

  @Override
  public Mono<GetTeam.Response> buildGetTeamResponse(Team team, boolean forPublicUser) {
    return teamProtoConverter
        .map(team, forPublicUser)
        .map(
            t -> {
              var builder = GetTeam.Response.newBuilder();
              builder.setTeam(t);
              return builder.build();
            });
  }

  @Override
  public Mono<CreateOrUpdateTeam.Response> buildCreateOrUpdateTeamResponse(
      Team team, SetUserTeamMembershipResponse setTeamMembershipResponse) {
    if (setTeamMembershipResponse.success()) {
      return teamProtoConverter
          .map(team)
          .map(t -> CreateOrUpdateTeam.Response.newBuilder().setTeam(t).build());
    } else {
      return Mono.just(
          CreateOrUpdateTeam.Response.newBuilder()
              .setErrors(buildValidationErrors(setTeamMembershipResponse))
              .build());
    }
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
    var errors = buildValidationErrors(r);
    if (errors != null) {
      builder.setErrors(errors);
    } else {
      builder.setSuccess(UpdateTeamMembers.Success.newBuilder().build());
    }
    return builder.build();
  }

  @Override
  public Team retrieveTeamModel(UUID id, CreateOrUpdateTeam.Request request) {
    return teamProtoConverter.retrieveUpdateModel(id, request);
  }

  private ValidationErrors buildValidationErrors(SetUserTeamMembershipResponse r) {
    switch (r) {
      case Success():
        {
          return null;
        }
      case ru.hse.lmsteam.backend.service.model.teams.ValidationErrors(
          var usersNotFound,
          var alreadyMembers):
        {
          var errorsBuilder = ru.hse.lmsteam.schema.api.teams.ValidationErrors.newBuilder();
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
                                var b =
                                    ru.hse.lmsteam.schema.api.teams.ValidationErrors.UserTeams
                                        .newBuilder();
                                b.setUser(userProtoConverter.toSnippet(entry.getKey()));
                                b.addAllTeams(
                                    entry.getValue().stream()
                                        .map(teamSnippetConverter::toSnippet)
                                        .toList());
                                return b.build();
                              })
                          .toList()));
          return errorsBuilder.build();
        }
      default:
        throw new IllegalStateException("Unexpected value: " + r);
    }
  }
}
