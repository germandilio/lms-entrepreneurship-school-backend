package ru.hse.lmsteam.backend.service.validation;

import ru.hse.lmsteam.backend.domain.Group;

public interface GroupValidator {
  void validateForSave(Group group);
}
