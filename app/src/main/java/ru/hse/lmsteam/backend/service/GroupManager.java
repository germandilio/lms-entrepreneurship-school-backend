package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.service.model.groups.GroupsFilterOptions;
import ru.hse.lmsteam.backend.service.model.groups.SetUserGroupMembershipResponse;

public interface GroupManager {
  Mono<Group> findById(Integer id);

  Mono<Group> update(Group group);

  Mono<Group> create(Group group);

  Mono<Long> delete(Integer groupId);

  Mono<Page<User>> getGroupMembers(Integer groupId, Pageable pageable);

  Mono<SetUserGroupMembershipResponse> updateGroupMembers(
      Integer groupId, ImmutableSet<UUID> userIds);

  Mono<Page<Group>> findAll(GroupsFilterOptions filterOptions, Pageable pageable);
}
