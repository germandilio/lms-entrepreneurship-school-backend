package ru.hse.lmsteam.backend.service.validation;

import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserRole;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;

@Component
public class UserValidatorImpl implements UserValidator {

  @Override
  public void validateForSave(User user) {
    if (user == null) {
      throw new BusinessLogicExpectationFailedException("User cannot be null");
    }
    if (user.name() == null || user.name().isEmpty()) {
      throw new BusinessLogicExpectationFailedException("Name cannot be null or empty");
    }
    if (user.surname() == null || user.surname().isEmpty()) {
      throw new BusinessLogicExpectationFailedException("Surname cannot be null or empty");
    }
    if (user.sex() == null) {
      throw new BusinessLogicExpectationFailedException("Sex cannot be null");
    }
    if (user.role() == null) {
      throw new BusinessLogicExpectationFailedException("Role cannot be null");
    }
    if (user.role() == UserRole.ADMIN) {
      throw new BusinessLogicExpectationFailedException("Admin role is not allowed");
    }
    if (user.email() == null || user.email().isEmpty()) {
      throw new BusinessLogicExpectationFailedException("Email cannot be null or empty");
    }
  }
}
