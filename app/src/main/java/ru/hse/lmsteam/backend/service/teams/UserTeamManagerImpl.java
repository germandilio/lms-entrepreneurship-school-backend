package ru.hse.lmsteam.backend.service.teams;

import com.google.common.collect.ImmutableMap;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.repository.UserTeamRepository;

@Service
@RequiredArgsConstructor
public class UserTeamManagerImpl implements UserTeamManager {
  private final UserTeamRepository userTeamRepository;

  /**
   * Get all user groups connections for each user, Relations is one to many.
   *
   * @param users list of users to search for
   * @return map <user, list<group>> for only users already in groups, if user is not in any group
   *     it will be omitted.
   */
  @Transactional(readOnly = true)
  @Override
  public Mono<Map<User, List<Team>>> getUserGroups(Collection<User> users) {
    if (users == null || users.isEmpty()) {
      return Mono.just(ImmutableMap.of());
    }
    var userMap = users.stream().collect(ImmutableMap.toImmutableMap(User::id, u -> u));
    return userTeamRepository
        .getUsersTeams(userMap.keySet())
        .collectList()
        .map(
            userTeams -> {
              var userGroupMap = new HashMap<User, List<Team>>(userMap.size() * 2);
              for (var userTeam : userTeams) {
                var user = userMap.get(userTeam.userId());
                if (user != null) {
                  userGroupMap.computeIfAbsent(user, u -> new ArrayList<>()).add(userTeam.team());
                }
              }
              return userGroupMap;
            });
  }
}
