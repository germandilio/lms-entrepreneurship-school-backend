package ru.hse.lmsteam.backend.domain.grades;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("grades")
public record GradeDB(
    @Id UUID id,
    UUID ownerId,
    UUID taskId,
    TaskType taskType,
    @Nullable UUID submissionId,
    @Nullable Integer adminGrade,
    String adminComment) {}
