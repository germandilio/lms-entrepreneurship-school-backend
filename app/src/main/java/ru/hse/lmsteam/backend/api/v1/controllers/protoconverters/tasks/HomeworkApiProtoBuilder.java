package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.schema.api.homeworks.*;
import ru.hse.lmsteam.schema.api.homeworks.GetHomework;

public interface HomeworkApiProtoBuilder {
  Mono<GetHomework.Response> buildGetHomeworkResponse(Homework group);

  Mono<CreateOrUpdateHomework.Response> buildCreateOrUpdateHomeworkResponse(Homework group);

  DeleteHomework.Response buildDeleteHomeworkResponse(long itemsDeleted);

  Mono<GetHomeworks.Response> buildGetHomeworksResponse(Page<Homework> assignments);

  Homework retrieveHomeworkModel(CreateOrUpdateHomework.Request request);
}
