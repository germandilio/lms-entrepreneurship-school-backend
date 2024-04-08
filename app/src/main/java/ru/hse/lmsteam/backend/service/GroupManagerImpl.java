package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableSet;
import jakarta.validation.ValidationException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.repository.GroupRepository;
import ru.hse.lmsteam.backend.repository.UserGroupRepository;
import ru.hse.lmsteam.backend.service.model.groups.GroupsFilterOptions;
import ru.hse.lmsteam.backend.service.model.groups.SetUserGroupMembershipResponse;
import ru.hse.lmsteam.backend.service.validation.GroupValidator;

@RequiredArgsConstructor
@Service
public class GroupManagerImpl implements GroupManager {
  private final GroupValidator groupValidator;
  private final GroupRepository groupRepository;
  private final UserGroupRepository userGroupRepository;
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
  public Mono<Group> update(final Group group) {
    if (group == null) {
      throw new IllegalArgumentException(
          "Group object is mandatory for update / create operations.");
    }
    groupValidator.validateForSave(group);
    return groupRepository
        .findById(group.id(), true)
        .map(dbGroup -> group.mergeWith(dbGroup, false))
        .flatMap(groupRepository::upsert)
        .flatMap(id -> groupRepository.findById(id, false))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new ValidationException(
                        "Group with number " + group.number() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
  }

  @Transactional
  @Override
  public Mono<Group> create(final Group group) {
    if (group == null) {
      throw new IllegalArgumentException(
          "Group object is mandatory for update / create operations.");
    }
    groupValidator.validateForSave(group);
    return groupRepository
        .upsert(group)
        .flatMap(id -> groupRepository.findById(id, false))
        .onErrorResume(
            exc -> {
              if (exc instanceof DuplicateKeyException) {
                return Mono.error(
                    new ValidationException(
                        "Group with number " + group.number() + " already exists"));
              } else {
                return Mono.error(exc);
              }
            });
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
    return userGroupRepository.getMembers(groupId);
  }

  @Transactional
  @Override
  public Mono<SetUserGroupMembershipResponse> updateGroupMembers(
      final Integer groupId, final ImmutableSet<UUID> userIds) {
    return userManager.setUserGroupMemberships(groupId, userIds);
  }

  @Transactional(readOnly = true)
  @Override
  public Mono<Page<Group>> findAll(
      final GroupsFilterOptions filterOptions, final Pageable pageable) {
    if (pageable == null) {
      throw new IllegalArgumentException(
          "Page parameters are mandatory. Please provide Pageable object with specified page params.");
    }
    var options = filterOptions == null ? new GroupsFilterOptions(null) : filterOptions;
    return groupRepository.findAll(options, pageable);
  }
}
