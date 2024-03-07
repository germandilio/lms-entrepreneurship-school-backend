package ru.hse.lmsteam.backend.service.validation;

import jakarta.validation.ValidationException;
import ru.hse.lmsteam.backend.domain.user.User;

/**
 * Validator for user entity. Each method throws an exception if the validation fails.
 *
 * @see ValidationException
 */
public interface UserValidator {

  void validateForSave(User user);
}
