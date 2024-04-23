package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson.LessonsApiProtoBuilder;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.schema.api.lessons.LessonSnippet;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;
import ru.hse.lmsteam.schema.api.tests.Test;
import ru.hse.lmsteam.schema.api.tests.TestSnippet;

@RequiredArgsConstructor
@Component
public class TestProtoConverterImpl implements TestProtoConverter {
  private final LessonManager lessonManager;
  private final LessonsApiProtoBuilder lessonsApiProtoBuilder;

  @Override
  public Mono<Test> map(ru.hse.lmsteam.backend.domain.tasks.Test task) {
    return lessonManager
        .findById(task.lessonId())
        .singleOptional()
        .handle(
            (lessonOpt, sink) -> {
              Test.Builder builder;
              try {
                builder = Test.parseFrom(task.payload()).toBuilder();
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
                  lesson -> builder.setLesson(lessonsApiProtoBuilder.toSnippet(lesson)));
              sink.next(builder.build());
            });
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Test map(Test task) {
    var builder = ru.hse.lmsteam.backend.domain.tasks.Test.builder();
    if (!task.getId().isBlank()) {
      builder.id(java.util.UUID.fromString(task.getId()));
    }
    if (task.hasLesson()) {
      builder.lessonId(java.util.UUID.fromString(task.getLesson().getId()));
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
  public Mono<TestSnippet> toSnippet(ru.hse.lmsteam.backend.domain.tasks.Test test) {
    return lessonManager
        .findById(test.lessonId())
        .singleOptional()
        .map(
            lessonOpt -> {
              var b = TestSnippet.newBuilder();
              b.setId(test.id().toString());
              b.setTitle(test.title());
              lessonOpt.ifPresent(lesson -> b.setLesson(lessonsApiProtoBuilder.toSnippet(lesson)));
              return b.build();
            });
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Test retrieveModel(
      CreateOrUpdateTest.Request request) {
    var b = Test.newBuilder();
    b.setLesson(LessonSnippet.newBuilder().setId(request.getLessonId()).build());
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
