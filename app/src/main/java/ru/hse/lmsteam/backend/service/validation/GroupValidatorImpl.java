package ru.hse.lmsteam.backend.service.validation;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user.Group;

@Component
public class GroupValidatorImpl implements GroupValidator {
  @Override
  public void validateForSave(Group group) {
    if (group == null) {
      throw new ValidationException("Group cannot be null");
    }

    if (group.id() == null) {
      throw new ValidationException("Id cannot be null");
    }

    if (group.number() == null) {
      throw new ValidationException("Group number cannot be null");
    }

    if (group.title() == null || group.title().isEmpty()) {
      throw new ValidationException("Title cannot be null or empty");
    }
  }
}
