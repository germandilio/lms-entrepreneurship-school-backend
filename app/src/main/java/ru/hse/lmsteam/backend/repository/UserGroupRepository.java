package ru.hse.lmsteam.backend.repository;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;

public interface UserGroupRepository {
  Flux<User> getMembers(Integer groupId);

  Flux<Group> getUserGroups(UUID userId);

  Flux<UUID> setUserGroupMemberships(Integer groupId, ImmutableSet<UUID> userIds);
}
