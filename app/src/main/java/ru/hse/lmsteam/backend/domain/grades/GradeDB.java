package ru.hse.lmsteam.backend.domain.grades;

import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("grades")
public record GradeDB(
    @Id UUID id,
    UUID ownerId,
    UUID taskId,
    TaskType taskType,
    @With @Nullable UUID submissionId,
    @Nullable Integer adminGrade,
    String adminComment) {}
