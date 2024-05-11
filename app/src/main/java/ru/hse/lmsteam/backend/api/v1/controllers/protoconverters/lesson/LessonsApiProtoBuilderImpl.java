package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.service.tasks.HomeworkManager;
import ru.hse.lmsteam.backend.service.tasks.TestManager;
import ru.hse.lmsteam.schema.api.lessons.LessonSnippet;

@Component
@RequiredArgsConstructor
public class LessonsApiProtoBuilderImpl implements LessonsApiProtoBuilder {
  private final HomeworkManager homeworkManager;
  private final TestManager testManager;

  @Override
  public Lesson toDomain(ru.hse.lmsteam.schema.api.lessons.Lesson lesson) {
    if (lesson == null) return null;
    var b = Lesson.builder();
    if (!lesson.getId().isBlank()) {
      b.id(UUID.fromString(lesson.getId()));
    }
    b.lessonNumber(lesson.getLessonNumber());
    b.title(lesson.getTitle());
    b.payload(lesson.toByteArray());
    var publishDate =
        lesson.hasPublishDate()
            ? Instant.ofEpochSecond(
                lesson.getPublishDate().getSeconds(), lesson.getPublishDate().getNanos())
            : Instant.now();
    b.publishDate(publishDate);
    return b.build();
  }

  @Override
  public Mono<ru.hse.lmsteam.schema.api.lessons.Lesson> toProto(Lesson lesson) {
    if (lesson == null) return Mono.empty();
    return Mono.zip(
            homeworkManager.findHomeworksByLesson(lesson.id()).collectList(),
            testManager.findTestsByLesson(lesson.id()).collectList())
        .handle(
            (tuple, sink) -> {
              var homeworks = tuple.getT1();
              var tests = tuple.getT2();
              try {
                var proto =
                    ru.hse.lmsteam.schema.api.lessons.Lesson.parseFrom(lesson.payload())
                        .toBuilder();

                sink.next(
                    proto
                        .addAllHomeworkIds(homeworks.stream().map(h -> h.id().toString()).toList())
                        .addAllTestIds(tests.stream().map(t -> t.id().toString()).toList())
                        .setId(lesson.id().toString())
                        .setLessonNumber(lesson.lessonNumber())
                        .setTitle(lesson.title())
                        .setPublishDate(Timestamps.fromMillis(lesson.publishDate().toEpochMilli()))
                        .build());
              } catch (InvalidProtocolBufferException e) {
                sink.error(new RuntimeException(e));
              }
            });
  }

  @Override
  public LessonSnippet toSnippet(Lesson lesson) {
    var b =
        LessonSnippet.newBuilder()
            .setId(lesson.id().toString())
            .setTitle(lesson.title())
            .setLessonNumber(lesson.lessonNumber());
    if (lesson.publishDate() != null) {
      b.setPublishDate(Timestamps.fromMillis(lesson.publishDate().toEpochMilli()));
    }
    return b.build();
  }
}
