package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.schema.api.lessons.LessonSnippet;

@Component
public class LessonsApiProtoBuilderImpl implements LessonsApiProtoBuilder {

  @Override
  public Lesson toDomain(ru.hse.lmsteam.schema.api.lessons.Lesson lesson) {
    if (lesson == null) return null;
    var id = lesson.getId().isBlank() ? null : UUID.fromString(lesson.getId());
    return new Lesson(
        id,
        lesson.getLessonNumber(),
        lesson.getTitle(),
        Instant.ofEpochSecond(
            lesson.getPublishDate().getSeconds(), lesson.getPublishDate().getNanos()),
        lesson.toByteArray());
  }

  @Override
  public ru.hse.lmsteam.schema.api.lessons.Lesson toProto(Lesson lesson) {
    try {
      if (lesson == null) return null;
      var proto = ru.hse.lmsteam.schema.api.lessons.Lesson.parseFrom(lesson.payload());
      return proto.toBuilder().setId(lesson.id().toString()).build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
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
