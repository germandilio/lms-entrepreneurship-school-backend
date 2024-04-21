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
  public Homework map(ru.hse.lmsteam.backend.domain.Homework homeAssignment) {
    try {
      var builder = Homework.parseFrom(homeAssignment.payload()).toBuilder();
      builder.setId(homeAssignment.id().toString());
      builder.setLessonId(homeAssignment.lessonId().toString());
      builder.setTitle(homeAssignment.title());
      if (homeAssignment.publishDate() != null) {
        builder.setPublishDate(Timestamps.fromMillis(homeAssignment.publishDate().toEpochMilli()));
      }
      if (homeAssignment.deadlineDate() != null) {
        builder.setDeadlineDate(
            Timestamps.fromMillis(homeAssignment.deadlineDate().toEpochMilli()));
      }
      builder.setIsGroupWork(homeAssignment.isGroup());
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ru.hse.lmsteam.backend.domain.Homework map(Homework homeAssignment) {
    var b = ru.hse.lmsteam.backend.domain.Homework.builder();
    if (!homeAssignment.getId().isBlank()) {
      b.id(UUID.fromString(homeAssignment.getId()));
    }
    if (!homeAssignment.getLessonId().isBlank()) {
      b.lessonId(UUID.fromString(homeAssignment.getLessonId()));
    }
    b.title(homeAssignment.getTitle());
    b.isGroup(homeAssignment.getIsGroupWork());
    b.payload(homeAssignment.toByteArray());

    if (homeAssignment.hasDeadlineDate()) {
      b.deadlineDate(Instant.ofEpochMilli(Timestamps.toMillis(homeAssignment.getDeadlineDate())));
    }
    if (homeAssignment.hasPublishDate()) {
      b.publishDate(Instant.ofEpochMilli(Timestamps.toMillis(homeAssignment.getPublishDate())));
    } else {
      b.publishDate(Instant.now());
    }
    return b.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.Homework retrieveModel(
      CreateOrUpdateHomework.Request request) {
    var b = Homework.newBuilder();
    b.setLessonId(request.getLessonId());
    b.setTitle(request.getTitle());
    b.setIsGroupWork(request.getIsGroupWork());
    if (request.hasPublishDate()) {
      b.setPublishDate(request.getPublishDate());
    }
    if (request.hasDeadlineDate()) {
      b.setDeadlineDate(request.getDeadlineDate());
    }
    return map(b.build());
  }
}
