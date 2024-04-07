package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.UserRole;
import ru.hse.lmsteam.backend.service.UserManager;
import ru.hse.lmsteam.schema.api.groups.CreateOrUpdateGroup;
import ru.hse.lmsteam.schema.api.groups.Group;
import ru.hse.lmsteam.schema.api.users.GroupSnippet;

@Component
@RequiredArgsConstructor
public class GroupProtoConverterImpl implements GroupProtoConverter {
  private final UserProtoConverter userProtoConverter;
  private final UserManager userManager;

  @Override
  public Group map(ru.hse.lmsteam.backend.domain.Group group) {
    var builder = Group.newBuilder();
    if (group.id() != null) {
      builder.setId(group.id());
    }
    if (group.number() != null) {
      builder.setNumber(group.number());
    }
    if (group.title() != null) {
      builder.setTitle(group.title());
    }
    if (group.description() != null) {
      builder.setDescription(group.description());
    }

    var members = userManager.findGroupMembers(group.id()).collectList().block();
    if (members != null) {
      builder.addAllStudents(
          members.stream()
              .filter(u -> UserRole.STUDENT.equals(u.role()))
              .map(userProtoConverter::map)
              .toList());
    }
    if (members != null) {
      builder.addAllTrackers(
          members.stream()
              .filter(u -> UserRole.TRACKER.equals(u.role()))
              .map(userProtoConverter::map)
              .toList());
    }

    return builder.build();
  }

  @Override
  public GroupSnippet toSnippet(ru.hse.lmsteam.backend.domain.Group group) {
    return GroupSnippet.newBuilder()
        .setId(group.id())
        .setNumber(group.number())
        .setTitle(group.title())
        .build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.Group retrieveUpdateModel(
      CreateOrUpdateGroup.Request request) {
    var builder = ru.hse.lmsteam.backend.domain.Group.builder();
    if (request.hasId()) {
      builder.id(request.getId().getValue());
    }
    if (request.hasNumber()) {
      builder.number(request.getNumber().getValue());
    }
    if (request.hasTitle()) {
      builder.title(request.getTitle().getValue());
    }
    if (request.hasDescription()) {
      builder.description(request.getDescription().getValue());
    }
    return builder.build();
  }
}
