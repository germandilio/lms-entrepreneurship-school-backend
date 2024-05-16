package ru.hse.lmsteam.backend.service.model.grades;

import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import ru.hse.lmsteam.backend.domain.grades.TaskType;

@Builder
public record GradesFilterOptions(
    @Nullable Integer gradeFrom,
    @Nullable Integer gradeTo,
    @Nullable UUID taskId,
    @Nullable UUID gradedByTrackerId,
    @Nullable Boolean gradedByAdmin,
    @Nullable Collection<UUID> ownersId,
    @Nullable TaskType taskType) {}
