package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user;

import java.util.Collection;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;
import ru.hse.lmsteam.schema.api.users.CreateOrUpdateUser;

public interface UserProtoConverter {
  ru.hse.lmsteam.schema.api.users.User map(User user, Collection<Team> userTeams);

  ru.hse.lmsteam.schema.api.users.User map(
      User user, Collection<Team> userTeams, boolean forPublicUser);

  ru.hse.lmsteam.schema.api.users.UserSnippet toSnippet(User user);

  User map(ru.hse.lmsteam.schema.api.users.User user);

  UserUpsertModel map(CreateOrUpdateUser.Request request);
}
