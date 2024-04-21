package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson;

import java.util.UUID;
import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.Lesson;
import ru.hse.lmsteam.schema.api.lessons.CreateLesson;
import ru.hse.lmsteam.schema.api.lessons.GetLesson;
import ru.hse.lmsteam.schema.api.lessons.GetLessons;

public interface LessonsApiProtoConverter {
  Lesson retrieveCreateModel(CreateLesson.Request request);

  ru.hse.lmsteam.schema.api.lessons.Lesson map(Lesson lesson);

  Lesson map(UUID id, ru.hse.lmsteam.schema.api.lessons.Lesson lesson);

  GetLessons.Response buildGetLessonsResponse(Page<Lesson> lessons);

  GetLesson.Response buildGetLessonResponse(Lesson lesson);
}
