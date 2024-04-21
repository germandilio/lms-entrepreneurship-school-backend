package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;

public interface ExamProtoConverter {
  ru.hse.lmsteam.schema.api.exams.Exam map(Exam task);

  Exam map(ru.hse.lmsteam.schema.api.exams.Exam task);

  Exam retrieveModel(CreateOrUpdateExam.Request request);
}
