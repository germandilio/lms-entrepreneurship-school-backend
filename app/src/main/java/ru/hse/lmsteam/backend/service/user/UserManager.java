package ru.hse.lmsteam.backend.service.user;

import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user_teams.User;
import ru.hse.lmsteam.backend.service.model.teams.SetUserTeamMembershipResponse;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;
import ru.hse.lmsteam.backend.service.model.user.UserUpsertModel;

public interface UserManager {
  Mono<User> findById(UUID id);

  Mono<Map<UUID, User>> findByIds(Collection<UUID> id);

  Flux<User> findTeamMembers(UUID teamId);

  Mono<User> update(UserUpsertModel user);

  Mono<User> create(UserUpsertModel user);

  Mono<Long> delete(UUID id);

  /**
   * Finds users by filter options and page properties.
   *
   * @param filterOptions filter options to filter users
   * @param pageable page properties such as page number, page size, sort order, etc.
   * @return users collection
   */
  Mono<Page<User>> findAll(UserFilterOptions filterOptions, Pageable pageable);

  Mono<BigDecimal> getUserBalance(UUID id);

  /**
   * Set user group memberships.
   *
   * @param groupId group which user will be added to
   * @param userIds list of users to be added to the group
   * @return @see SetUserGroupMembershipResponse
   */
  Mono<SetUserTeamMembershipResponse> setUserTeamMemberships(
      UUID groupId, ImmutableSet<UUID> userIds);

  Mono<SetUserTeamMembershipResponse> validateUserTeamMemberships(
      UUID groupId, ImmutableSet<UUID> userIds);
}
