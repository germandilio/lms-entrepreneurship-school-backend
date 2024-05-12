package ru.hse.lmsteam.backend.domain.grades;

import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("tracker_grades")
public record TrackerGradeDb(
    @Id UUID gradeId, UUID trackerId, @Nullable Integer grade, @Nullable String comment) {}
