package ru.hse.lmsteam.backend.service.model.teams;

import java.util.UUID;
import ru.hse.lmsteam.backend.domain.user_teams.Team;

public record UserTeam(UUID userId, Team team) {}
