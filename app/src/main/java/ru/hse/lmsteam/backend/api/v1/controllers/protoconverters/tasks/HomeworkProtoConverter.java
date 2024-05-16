package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.schema.api.homeworks.CreateOrUpdateHomework;

public interface HomeworkProtoConverter {
  Mono<ru.hse.lmsteam.schema.api.homeworks.Homework> map(Homework homeAssignment);

  Homework map(ru.hse.lmsteam.schema.api.homeworks.Homework homeAssignment);

  ru.hse.lmsteam.schema.api.homeworks.HomeworkSnippet toSnippet(
      Homework homeAssignment, Lesson lesson);

  Homework retrieveModel(CreateOrUpdateHomework.Request request);
}
