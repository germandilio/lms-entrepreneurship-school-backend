package ru.hse.lmsteam.backend.service;

import com.google.common.collect.ImmutableList;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.user.Group;
import ru.hse.lmsteam.backend.domain.user.User;
import ru.hse.lmsteam.backend.service.model.GroupsFilterOptions;

public interface GroupManager {
  Mono<Group> findById(Integer id);

  Mono<Group> upsert(Group group);

  Mono<Group> delete(Group group);

  Flux<User> getGroupMembers(Integer groupId);

  Flux<User> updateGroupMembers(Integer groupId, ImmutableList<UUID> userIds);

  Flux<Group> findAll(GroupsFilterOptions filterOptions, Pageable pageable);
}
