package ru.hse.lmsteam.backend.service.validation;

import ru.hse.lmsteam.backend.domain.user_teams.Team;

public interface TeamValidator {
  void validateForSave(Team team);
}
