package ru.hse.lmsteam.backend.service.validation;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.user.User;

@Component
public class UserValidatorImpl implements UserValidator {

  @Override
  public void validateForSave(User user) {
    if (user == null) {
      throw new ValidationException("User is null");
    }
    if (user.name() == null || user.name().isEmpty()) {
      throw new ValidationException("Name is null or empty");
    }
    if (user.surname() == null || user.surname().isEmpty()) {
      throw new ValidationException("Surname is null or empty");
    }
    if (user.sex() == null) {
      throw new ValidationException("Sex is null");
    }

    if (user.email() == null || user.email().isEmpty()) {
      throw new ValidationException("Email is null or empty");
    }
  }
}
