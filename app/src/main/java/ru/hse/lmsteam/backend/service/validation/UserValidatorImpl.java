package ru.hse.lmsteam.backend.service.validation;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.User;
import ru.hse.lmsteam.backend.domain.UserRole;

@Component
public class UserValidatorImpl implements UserValidator {

  @Override
  public void validateForSave(User user) {
    if (user == null) {
      throw new ValidationException("User cannot be null");
    }
    if (user.name() == null || user.name().isEmpty()) {
      throw new ValidationException("Name cannot be null or empty");
    }
    if (user.surname() == null || user.surname().isEmpty()) {
      throw new ValidationException("Surname cannot be null or empty");
    }
    if (user.sex() == null) {
      throw new ValidationException("Sex cannot be null");
    }
    if (user.role() == null) {
      throw new ValidationException("Role cannot be null");
    }
    if (user.role() != null && user.role() == UserRole.ADMIN) {
      throw new ValidationException("Admin role is not allowed");
    }
    if (user.email() == null || user.email().isEmpty()) {
      throw new ValidationException("Email cannot be null or empty");
    }
  }
}
