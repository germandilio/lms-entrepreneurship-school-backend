package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import java.time.Instant;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;
import ru.hse.lmsteam.schema.api.exams.Exam;
import ru.hse.lmsteam.schema.api.exams.ExamSnippet;

@Component
public class ExamProtoConverterImpl implements ExamProtoConverter {

  @Override
  public Exam map(ru.hse.lmsteam.backend.domain.tasks.Exam task) {
    try {
      var builder = Exam.parseFrom(task.payload()).toBuilder();
      builder.setId(task.id().toString());
      builder.setTitle(task.title());
      if (task.publishDate() != null) {
        builder.setPublishDate(Timestamps.fromMillis(task.publishDate().toEpochMilli()));
      }
      if (task.deadlineDate() != null) {
        builder.setDeadlineDate(Timestamps.fromMillis(task.deadlineDate().toEpochMilli()));
      }
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Exam map(Exam task) {
    var b = ru.hse.lmsteam.backend.domain.tasks.Exam.builder();
    if (!task.getId().isBlank()) {
      b.id(java.util.UUID.fromString(task.getId()));
    }
    b.title(task.getTitle());
    b.payload(task.toByteArray());

    if (task.hasDeadlineDate()) {
      b.deadlineDate(Instant.ofEpochMilli(Timestamps.toMillis(task.getDeadlineDate())));
    } else {
      throw new BusinessLogicExpectationFailedException("NO_DEADLINE_DATE");
    }
    if (task.hasPublishDate()) {
      b.publishDate(Instant.ofEpochMilli(Timestamps.toMillis(task.getPublishDate())));
    } else {
      b.publishDate(Instant.now());
    }
    return b.build();
  }

  @Override
  public ExamSnippet toSnippet(ru.hse.lmsteam.backend.domain.tasks.Exam task) {
    var b = ExamSnippet.newBuilder();
    b.setId(task.id().toString());
    b.setTitle(task.title());
    if (task.deadlineDate() != null) {
      b.setDeadlineDate(Timestamps.fromMillis(task.deadlineDate().toEpochMilli()));
    }
    return b.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Exam retrieveModel(
      CreateOrUpdateExam.Request request) {
    var b = Exam.newBuilder();
    b.setTitle(request.getTitle());
    b.setDescription(request.getDescription());
    b.setGradingCriteria(request.getGradingCriteria());
    b.addAllExternalMaterialUrls(request.getExternalMaterialUrlsList());
    if (request.hasDeadlineDate()) {
      b.setDeadlineDate(request.getDeadlineDate());
    }
    if (request.hasPublishDate()) {
      b.setPublishDate(request.getPublishDate());
    }
    return map(b.build());
  }
}
