package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters;

import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.schema.api.lessons.CreateLesson;
import ru.hse.lmsteam.schema.api.lessons.GetLessons;

public interface LessonsApiProtoConverter {
  Lesson retrieveUpdateModel(CreateLesson.Request request);

  ru.hse.lmsteam.schema.api.lessons.Lesson map(Lesson lesson) throws InvalidProtocolBufferException;

  Lesson map(ru.hse.lmsteam.schema.api.lessons.Lesson lesson);

  GetLessons.Response buildGetLessonsResponse(Page<Lesson> lessons);
}
