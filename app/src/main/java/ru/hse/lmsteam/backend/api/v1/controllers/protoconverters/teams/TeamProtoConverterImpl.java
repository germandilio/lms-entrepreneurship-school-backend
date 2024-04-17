package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UserProtoConverter;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserRole;
import ru.hse.lmsteam.backend.service.user.UserManager;
import ru.hse.lmsteam.schema.api.teams.CreateOrUpdateTeam;

@Component
@RequiredArgsConstructor
public class TeamProtoConverterImpl implements TeamProtoConverter {
  private final UserProtoConverter userProtoConverter;
  private final UserManager userManager;

  @Override
  public ru.hse.lmsteam.schema.api.teams.Team map(Team team, boolean forPublicUser) {
    var builder = ru.hse.lmsteam.schema.api.teams.Team.newBuilder();
    if (team.id() != null) {
      builder.setId(team.id().toString());
    }
    if (team.number() != null) {
      builder.setNumber(team.number());
    }
    if (team.projectTheme() != null) {
      builder.setProjectTheme(team.projectTheme());
    }
    if (team.description() != null) {
      builder.setDescription(team.description());
    }

    List<User> members = null;
    try {
      members = userManager.findTeamMembers(team.id()).collectList().toFuture().get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (members != null) {
      builder.addAllStudents(
          members.stream()
              .filter(u -> UserRole.LEARNER.equals(u.role()))
              .map(u -> userProtoConverter.map(u, forPublicUser))
              .toList());
    }
    if (members != null) {
      builder.addAllTrackers(
          members.stream()
              .filter(u -> UserRole.TRACKER.equals(u.role()))
              .map(u -> userProtoConverter.map(u, forPublicUser))
              .toList());
    }

    return builder.build();
  }

  @Override
  public ru.hse.lmsteam.schema.api.teams.Team map(Team team) {
    return map(team, false);
  }

  @Override
  public Team retrieveUpdateModel(UUID id, CreateOrUpdateTeam.Request request) {
    var builder = Team.builder();
    if (id != null) {
      builder.id(id);
    }
    if (request.hasNumber()) {
      builder.number(request.getNumber().getValue());
    }
    if (request.hasProjectTheme()) {
      builder.projectTheme(request.getProjectTheme().getValue());
    }
    if (request.hasDescription()) {
      builder.description(request.getDescription().getValue());
    }
    return builder.build();
  }
}
