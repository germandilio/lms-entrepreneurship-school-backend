package ru.hse.lmsteam.backend.service.validation;

import ru.hse.lmsteam.backend.domain.user.Group;

public interface GroupValidator {
  void validateForSave(Group group);
}
