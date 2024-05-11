package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams;

import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.schema.api.teams.TeamSnippet;

@Component
public class TeamSnippetConverter {
  public TeamSnippet toSnippet(Team team) {
    return TeamSnippet.newBuilder()
        .setId(team.id().toString())
        .setNumber(team.number())
        .setProjectTheme(team.projectTheme())
        .setDescription(team.description())
        .build();
  }
}
