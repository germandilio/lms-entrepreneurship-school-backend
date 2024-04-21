package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.Homework;
import ru.hse.lmsteam.schema.api.homeworks.*;
import ru.hse.lmsteam.schema.api.homeworks.GetHomework;

public interface HomeworkApiProtoBuilder {
  GetHomework.Response buildGetHomeworkResponse(Homework group);

  CreateOrUpdateHomework.Response buildCreateHomeworkResponse(Homework group);

  CreateOrUpdateHomework.Response buildUpdateHomeworkResponse(Homework group);

  DeleteHomework.Response buildDeleteHomeworkResponse(long itemsDeleted);

  GetHomeworks.Response buildGetHomeworksResponse(Page<Homework> assignments);

  Homework retrieveHomeworkModel(CreateOrUpdateHomework.Request request);

  Homework map(ru.hse.lmsteam.schema.api.homeworks.Homework homeAssignment);
}
