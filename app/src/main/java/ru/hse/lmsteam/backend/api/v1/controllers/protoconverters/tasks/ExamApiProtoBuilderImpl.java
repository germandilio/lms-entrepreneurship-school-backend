package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;
import ru.hse.lmsteam.schema.api.exams.DeleteExam;
import ru.hse.lmsteam.schema.api.exams.GetExam;
import ru.hse.lmsteam.schema.api.exams.GetExams;

@RequiredArgsConstructor
@Component
public class ExamApiProtoBuilderImpl implements ExamApiProtoBuilder {
  private final ExamProtoConverter examProtoConverter;

  @Override
  public GetExam.Response buildGetExamResponse(Exam exam) {
    var b = GetExam.Response.newBuilder();
    if (exam != null) {
      b.setExam(examProtoConverter.map(exam));
    }
    return b.build();
  }

  @Override
  public GetExams.Response buildGetExamsResponse(Page<Exam> exams) {
    var b = GetExams.Response.newBuilder();
    b.setPage(
        ru.hse.lmsteam.schema.api.common.Page.newBuilder()
            .setTotalPages(exams.getTotalPages())
            .setTotalElements(exams.getTotalElements())
            .build());
    b.addAllExams(exams.map(examProtoConverter::toSnippet));
    return b.build();
  }

  @Override
  public CreateOrUpdateExam.Response buildCreateOrUpdateExamResponse(Exam exam) {
    var b = CreateOrUpdateExam.Response.newBuilder();
    if (exam != null) {
      b.setExam(examProtoConverter.map(exam));
    }
    return b.build();
  }

  @Override
  public DeleteExam.Response buildDeleteExamResponse(long itemsDeleted) {
    return DeleteExam.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public Exam retrieveExamModel(CreateOrUpdateExam.Request request) {
    return examProtoConverter.retrieveModel(request);
  }
}
