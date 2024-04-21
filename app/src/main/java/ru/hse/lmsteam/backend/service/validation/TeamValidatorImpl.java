package ru.hse.lmsteam.backend.service.validation;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.Team;

@Component
public class TeamValidatorImpl implements TeamValidator {
  @Override
  public void validateForSave(Team team) {
    if (team == null) {
      throw new ValidationException("Group cannot be null");
    }

    if (team.number() == null) {
      throw new ValidationException("Group number cannot be null");
    }

    if (team.projectTheme() == null || team.projectTheme().isBlank()) {
      throw new ValidationException("Title cannot be null or blank");
    }
  }
}
