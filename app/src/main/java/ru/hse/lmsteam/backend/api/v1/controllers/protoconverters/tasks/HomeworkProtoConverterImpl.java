package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson.LessonsProtoConverter;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.schema.api.homeworks.CreateOrUpdateHomework;
import ru.hse.lmsteam.schema.api.homeworks.Homework;
import ru.hse.lmsteam.schema.api.homeworks.HomeworkSnippet;
import ru.hse.lmsteam.schema.api.lessons.LessonSnippet;

@RequiredArgsConstructor
@Component
public class HomeworkProtoConverterImpl implements HomeworkProtoConverter {
  private final LessonManager lessonManager;
  private final LessonsProtoConverter lessonsProtoConverter;

  @Override
  public Mono<Homework> map(ru.hse.lmsteam.backend.domain.tasks.Homework task) {
    return lessonManager
        .findById(task.lessonId())
        .singleOptional()
        .handle(
            (lessonOpt, sink) -> {
              Homework.Builder builder;
              try {
                builder = Homework.parseFrom(task.payload()).toBuilder();
              } catch (InvalidProtocolBufferException e) {
                sink.error(new RuntimeException(e));
                return;
              }

              builder.setId(task.id().toString());
              builder.setTitle(task.title());
              if (task.publishDate() != null) {
                builder.setPublishDate(Timestamps.fromMillis(task.publishDate().toEpochMilli()));
              }
              if (task.deadlineDate() != null) {
                builder.setDeadlineDate(Timestamps.fromMillis(task.deadlineDate().toEpochMilli()));
              }
              lessonOpt.ifPresent(
                  lesson -> builder.setLesson(lessonsProtoConverter.toSnippet(lesson)));
              sink.next(builder.build());
            });
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Homework map(Homework task) {
    var b = ru.hse.lmsteam.backend.domain.tasks.Homework.builder();
    if (!task.getId().isBlank()) {
      b.id(UUID.fromString(task.getId()));
    }
    if (task.hasLesson()) {
      b.lessonId(UUID.fromString(task.getLesson().getId()));
    }
    b.title(task.getTitle());
    b.isGroup(task.getIsGroupWork());
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
  public HomeworkSnippet toSnippet(
      ru.hse.lmsteam.backend.domain.tasks.Homework homeAssignment, Lesson lesson) {
    var b = HomeworkSnippet.newBuilder();
    b.setId(homeAssignment.id().toString());
    b.setTitle(homeAssignment.title());
    if (homeAssignment.deadlineDate() != null) {
      b.setDeadlineDate(Timestamps.fromMillis(homeAssignment.deadlineDate().toEpochMilli()));
    }
    if (lesson != null) {
      b.setLesson(lessonsProtoConverter.toSnippet(lesson));
    }
    return b.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Homework retrieveModel(
      CreateOrUpdateHomework.Request request) {
    var b = Homework.newBuilder();
    b.setLesson(LessonSnippet.newBuilder().setId(request.getLessonId()).build());
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
