package ru.hse.lmsteam.backend.service.validation;

import org.springframework.stereotype.Service;

@Service
public class PasswordValidator {
  public void validate(String password) {
    if (password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters long");
    }
  }
}
