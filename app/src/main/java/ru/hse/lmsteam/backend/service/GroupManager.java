package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableSet;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.Group;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.GroupsFilterOptions;

public interface GroupManager {
  Mono<Group> findById(Integer id);

  Mono<Group> upsert(Group group);

  Mono<Long> delete(Integer groupId);

  Mono<Page<User>> getGroupMembers(Integer groupId);

  Flux<User> updateGroupMembers(Integer groupId, ImmutableSet<UUID> userIds);

  Mono<Page<Group>> findAll(GroupsFilterOptions filterOptions, Pageable pageable);
}
