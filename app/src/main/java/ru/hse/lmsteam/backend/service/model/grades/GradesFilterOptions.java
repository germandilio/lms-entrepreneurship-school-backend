package ru.hse.lmsteam.backend.service.model.grades;

import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
public record GradesFilterOptions(
    @Nullable Integer gradeFrom,
    @Nullable Integer gradeTo,
    @Nullable UUID taskId,
    @Nullable UUID ownerId,
    @Nullable UUID trackerId) {}
