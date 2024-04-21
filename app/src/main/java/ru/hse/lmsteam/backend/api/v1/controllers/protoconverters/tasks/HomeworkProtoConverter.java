package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import ru.hse.lmsteam.backend.domain.Homework;
import ru.hse.lmsteam.schema.api.homeworks.CreateOrUpdateHomework;

public interface HomeworkProtoConverter {
  ru.hse.lmsteam.schema.api.homeworks.Homework map(Homework homeAssignment);

  Homework map(ru.hse.lmsteam.schema.api.homeworks.Homework homeAssignment);

  Homework retrieveModel(CreateOrUpdateHomework.Request request);
}
