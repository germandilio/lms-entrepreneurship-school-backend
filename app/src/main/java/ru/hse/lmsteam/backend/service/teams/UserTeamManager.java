package ru.hse.lmsteam.backend.service.teams;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.Team;
import ru.hse.lmsteam.backend.domain.user_teams.User;

public interface UserTeamManager {
  Mono<Map<User, List<Team>>> getUserGroups(Collection<User> users);
}
