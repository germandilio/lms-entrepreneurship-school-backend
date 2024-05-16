package ru.hse.lmsteam.backend.domain.grades;

import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import ru.hse.lmsteam.backend.domain.user_teams.User;

@Builder
public record TrackerGrade(
    User tracker, @Nullable Integer trackerGrade, @Nullable String trackerComment) {}
