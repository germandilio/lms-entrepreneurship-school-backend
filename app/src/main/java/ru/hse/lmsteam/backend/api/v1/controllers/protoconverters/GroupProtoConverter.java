package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import ru.hse.lmsteam.backend.domain.Group;
import ru.hse.lmsteam.schema.api.groups.CreateOrUpdateGroup;

public interface GroupProtoConverter {
  ru.hse.lmsteam.schema.api.groups.Group map(Group group);

  ru.hse.lmsteam.schema.api.users.GroupSnippet toSnippet(Group group);

  Group retrieveUpdateModel(CreateOrUpdateGroup.Request request);
}
