package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.schema.api.homeworks.*;
import ru.hse.lmsteam.schema.api.homeworks.GetHomework;

@RequiredArgsConstructor
@Component
public class HomeworkApiProtoBuilderImpl implements HomeworkApiProtoBuilder {
  private final HomeworkProtoConverter homeworkProtoConverter;

  @Override
  public GetHomework.Response buildGetHomeworkResponse(Homework homework) {
    var b = GetHomework.Response.newBuilder();
    if (homework != null) {
      b.setHomework(homeworkProtoConverter.map(homework));
    }
    return b.build();
  }

  @Override
  public CreateOrUpdateHomework.Response buildCreateHomeworkResponse(Homework group) {
    var b = CreateOrUpdateHomework.Response.newBuilder();
    if (group != null) {
      b.setHomework(homeworkProtoConverter.map(group));
    }
    return b.build();
  }

  @Override
  public CreateOrUpdateHomework.Response buildUpdateHomeworkResponse(Homework group) {
    var b = CreateOrUpdateHomework.Response.newBuilder();
    if (group != null) {
      b.setHomework(homeworkProtoConverter.map(group));
    }
    return b.build();
  }

  @Override
  public DeleteHomework.Response buildDeleteHomeworkResponse(long itemsDeleted) {
    return DeleteHomework.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public GetHomeworks.Response buildGetHomeworksResponse(Page<Homework> assignments) {
    return GetHomeworks.Response.newBuilder()
        .setPage(
            ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                .setTotalPages(assignments.getTotalPages())
                .setTotalElements(assignments.getTotalElements())
                .build())
        .addAllHomeworks(assignments.map(homeworkProtoConverter::map))
        .build();
  }

  @Override
  public Homework retrieveHomeworkModel(CreateOrUpdateHomework.Request request) {
    return homeworkProtoConverter.retrieveModel(request);
  }
}
