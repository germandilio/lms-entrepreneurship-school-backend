package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.Group;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.repository.GroupRepository;
import ru.hse.lmsteam.backend.service.model.GroupsFilterOptions;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

@RequiredArgsConstructor
@Service
public class GroupManagerImpl implements GroupManager {
  private final GroupRepository groupRepository;
  private final UserManager userManager;

  @Transactional(readOnly = true)
  @Override
  public Mono<Group> findById(final Integer id) {
    if (id == null) {
      return Mono.empty();
    }
    return groupRepository.findById(id);
  }

  @Transactional
  @Override
  public Mono<Group> upsert(final Group group) {
    if (group == null) {
      return Mono.empty();
    }
    return groupRepository.upsert(group).flatMap(id -> groupRepository.findById(id, false));
  }

  @Transactional
  @Override
  public Mono<Long> delete(final Integer id) {
    if (id == null) {
      return Mono.just(0L);
    }
    return groupRepository.delete(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<User> getGroupMembers(final Integer groupId) {
    var filterOptions = new UserFilterOptions(null, null, ImmutableSet.of(groupId), null, null);
    var pageable = Pageable.unpaged();
    return userManager.findUsers(filterOptions, pageable);
  }

  @Transactional
  @Override
  public Flux<User> updateGroupMembers(final Integer groupId, final ImmutableSet<UUID> userIds) {
    return userManager.setUserGroupMemberships(groupId, userIds);
  }

  @Transactional(readOnly = true)
  @Override
  public Flux<Group> findAll(final GroupsFilterOptions filterOptions, final Pageable pageable) {
    if (pageable == null) {
      throw new IllegalArgumentException(
          "Page parameters are mandatory. Please provide Pageable object with specified page params.");
    }
    var options = filterOptions == null ? new GroupsFilterOptions(null) : filterOptions;
    return groupRepository.findAll(options, pageable);
  }
}
