package ru.hse.lmsteam.backend.service.validation;

import org.springframework.stereotype.Service;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;

@Service
public class PasswordValidator {
  public void validate(String password) {
    if (password.length() < 8) {
      throw new BusinessLogicExpectationFailedException(
          "Password must be at least 8 characters long");
    }
  }
}
