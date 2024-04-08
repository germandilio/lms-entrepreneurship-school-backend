package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import org.springframework.stereotype.Component;
import ru.hse.lmsteam.schema.api.users.GroupSnippet;

@Component
public class GroupSnippetConverter {
  public GroupSnippet toSnippet(ru.hse.lmsteam.backend.domain.Group group) {
    return GroupSnippet.newBuilder()
        .setId(group.id())
        .setNumber(group.number())
        .setTitle(group.title())
        .build();
  }
}
