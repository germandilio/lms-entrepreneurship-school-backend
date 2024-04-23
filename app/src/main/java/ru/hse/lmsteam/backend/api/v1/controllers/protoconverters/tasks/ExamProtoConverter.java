package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;
import ru.hse.lmsteam.schema.api.exams.ExamSnippet;

public interface ExamProtoConverter {
  ru.hse.lmsteam.schema.api.exams.Exam map(Exam task);

  Exam map(ru.hse.lmsteam.schema.api.exams.Exam task);

  ExamSnippet toSnippet(ru.hse.lmsteam.backend.domain.tasks.Exam task);

  Exam retrieveModel(CreateOrUpdateExam.Request request);
}
