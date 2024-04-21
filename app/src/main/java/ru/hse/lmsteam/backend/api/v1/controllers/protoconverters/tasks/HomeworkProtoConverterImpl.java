package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.schema.api.homeworks.CreateOrUpdateHomework;
import ru.hse.lmsteam.schema.api.homeworks.Homework;

@Component
public class HomeworkProtoConverterImpl implements HomeworkProtoConverter {
  @Override
  public Homework map(ru.hse.lmsteam.backend.domain.tasks.Homework task) {
    try {
      var builder = Homework.parseFrom(task.payload()).toBuilder();
      builder.setId(task.id().toString());
      builder.setLessonId(task.lessonId().toString());
      builder.setTitle(task.title());
      if (task.publishDate() != null) {
        builder.setPublishDate(Timestamps.fromMillis(task.publishDate().toEpochMilli()));
      }
      if (task.deadlineDate() != null) {
        builder.setDeadlineDate(Timestamps.fromMillis(task.deadlineDate().toEpochMilli()));
      }
      builder.setIsGroupWork(task.isGroup());
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Homework map(Homework task) {
    var b = ru.hse.lmsteam.backend.domain.tasks.Homework.builder();
    if (!task.getId().isBlank()) {
      b.id(UUID.fromString(task.getId()));
    }
    if (!task.getLessonId().isBlank()) {
      b.lessonId(UUID.fromString(task.getLessonId()));
    }
    b.title(task.getTitle());
    b.isGroup(task.getIsGroupWork());
    b.payload(task.toByteArray());

    if (task.hasDeadlineDate()) {
      b.deadlineDate(Instant.ofEpochMilli(Timestamps.toMillis(task.getDeadlineDate())));
    }
    if (task.hasPublishDate()) {
      b.publishDate(Instant.ofEpochMilli(Timestamps.toMillis(task.getPublishDate())));
    } else {
      b.publishDate(Instant.now());
    }
    return b.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Homework retrieveModel(
      CreateOrUpdateHomework.Request request) {
    var b = Homework.newBuilder();
    b.setLessonId(request.getLessonId());
    b.setTitle(request.getTitle());
    b.setDescription(request.getDescription());
    b.setGradingCriteria(request.getGradingCriteria());
    b.setIsGroupWork(request.getIsGroupWork());
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