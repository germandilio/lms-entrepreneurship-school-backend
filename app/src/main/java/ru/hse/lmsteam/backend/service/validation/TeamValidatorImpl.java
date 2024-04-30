package ru.hse.lmsteam.backend.service.validation;

import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.Team;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;

@Component
public class TeamValidatorImpl implements TeamValidator {
  @Override
  public void validateForSave(Team team) {
    if (team == null) {
      throw new BusinessLogicExpectationFailedException("Group cannot be null");
    }

    if (team.number() == null) {
      throw new BusinessLogicExpectationFailedException("Group number cannot be null");
    }

    if (team.projectTheme() == null || team.projectTheme().isBlank()) {
      throw new BusinessLogicExpectationFailedException("Title cannot be null or blank");
    }
  }
}
