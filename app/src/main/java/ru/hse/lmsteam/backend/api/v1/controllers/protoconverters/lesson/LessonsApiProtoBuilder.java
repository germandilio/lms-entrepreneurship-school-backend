package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.hse.lmsteam.backend.domain.Lesson;

public interface LessonsApiProtoBuilder {
  Lesson toDomain(ru.hse.lmsteam.schema.api.lessons.Lesson lesson);

  ru.hse.lmsteam.schema.api.lessons.Lesson toProto(Lesson lesson)
      throws InvalidProtocolBufferException;
}
