package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

public interface GroupProtoConverter {
  ru.hse.lmsteam.schema.api.groups.Group map(ru.hse.lmsteam.backend.domain.user.Group group);

  ru.hse.lmsteam.backend.domain.user.Group map(ru.hse.lmsteam.schema.api.groups.Group group);
}
