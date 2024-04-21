package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;
import ru.hse.lmsteam.schema.api.exams.DeleteExam;
import ru.hse.lmsteam.schema.api.exams.GetExam;
import ru.hse.lmsteam.schema.api.exams.GetExams;

public interface ExamApiProtoBuilder {
  GetExam.Response buildGetExamResponse(Exam exam);

  GetExams.Response buildGetExamsResponse(Page<Exam> exams);

  CreateOrUpdateExam.Response buildCreateOrUpdateExamResponse(Exam exam);

  DeleteExam.Response buildDeleteExamResponse(long itemsDeleted);

  Exam retrieveExamModel(CreateOrUpdateExam.Request request);
}
