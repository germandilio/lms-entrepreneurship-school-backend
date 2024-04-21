package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;
import ru.hse.lmsteam.schema.api.tests.Test;

@Component
public class TestProtoConverterImpl implements TestProtoConverter {
  @Override
  public Test map(ru.hse.lmsteam.backend.domain.tasks.Test task) {
    try {
      var builder = Test.parseFrom(task.payload()).toBuilder();
      builder.setId(task.id().toString());
      builder.setLessonId(task.lessonId().toString());
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
  public ru.hse.lmsteam.backend.domain.tasks.Test map(Test task) {
    var builder = ru.hse.lmsteam.backend.domain.tasks.Test.builder();
    if (!task.getId().isBlank()) {
      builder.id(java.util.UUID.fromString(task.getId()));
    }
    if (!task.getLessonId().isBlank()) {
      builder.lessonId(java.util.UUID.fromString(task.getLessonId()));
    }
    if (task.hasPublishDate()) {
      builder.publishDate(
          java.time.Instant.ofEpochMilli(Timestamps.toMillis(task.getPublishDate())));
    }
    if (task.hasDeadlineDate()) {
      builder.deadlineDate(
          java.time.Instant.ofEpochMilli(Timestamps.toMillis(task.getDeadlineDate())));
    }
    builder.title(task.getTitle());
    builder.payload(task.toByteArray());
    return builder.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Test retrieveModel(
      CreateOrUpdateTest.Request request) {
    var b = Test.newBuilder();
    b.setLessonId(request.getLessonId());
    b.setTitle(request.getTitle());
    b.setDescription(request.getDescription());
    b.setGradingCriteria(request.getGradingCriteria());
    b.addAllExternalMaterialUrls(request.getExternalMaterialUrlsList());
    if (request.hasPublishDate()) {
      b.setPublishDate(request.getPublishDate());
    }
    if (request.hasDeadlineDate()) {
      b.setDeadlineDate(request.getDeadlineDate());
    }
    return map(b.build());
  }
}
