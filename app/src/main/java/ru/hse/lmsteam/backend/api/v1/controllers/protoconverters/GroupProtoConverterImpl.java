package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import org.springframework.stereotype.Component;
import ru.hse.lmsteam.schema.api.groups.CreateOrUpdateGroup;
import ru.hse.lmsteam.schema.api.groups.Group;

@Component
public class GroupProtoConverterImpl implements GroupProtoConverter {
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
    if (group.isDeleted() != null) {
      builder.setIsDeleted(group.isDeleted());
    }

    return builder.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.Group map(Group group) {
    var builder = ru.hse.lmsteam.backend.domain.Group.builder();
    builder.id(group.getId());
    builder.number(group.getNumber());
    builder.title(group.getTitle());
    builder.description(group.getDescription());
    builder.isDeleted(group.getIsDeleted());
    return builder.build();
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
    if (request.hasIsDeleted()) {
      builder.isDeleted(request.getIsDeleted().getValue());
    }
    return builder.build();
  }
}
