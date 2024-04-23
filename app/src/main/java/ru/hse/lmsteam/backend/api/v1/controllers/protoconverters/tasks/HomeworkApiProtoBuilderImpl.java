package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.schema.api.homeworks.*;
import ru.hse.lmsteam.schema.api.homeworks.GetHomework;

@RequiredArgsConstructor
@Component
public class HomeworkApiProtoBuilderImpl implements HomeworkApiProtoBuilder {
  private final HomeworkProtoConverter homeworkProtoConverter;

  @Override
  public Mono<GetHomework.Response> buildGetHomeworkResponse(Homework homework) {
    return homeworkProtoConverter
        .map(homework)
        .map(homeworkProto -> GetHomework.Response.newBuilder().setHomework(homeworkProto).build());
  }

  @Override
  public Mono<CreateOrUpdateHomework.Response> buildCreateOrUpdateHomeworkResponse(Homework group) {
    return homeworkProtoConverter
        .map(group)
        .map(
            homeworkProto ->
                CreateOrUpdateHomework.Response.newBuilder().setHomework(homeworkProto).build());
  }

  @Override
  public DeleteHomework.Response buildDeleteHomeworkResponse(long itemsDeleted) {
    return DeleteHomework.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public Mono<GetHomeworks.Response> buildGetHomeworksResponse(Page<Homework> assignments) {
    return Flux.fromIterable(assignments.toList())
        .flatMap(homeworkProtoConverter::toSnippet)
        .collectList()
        .map(
            snippets ->
                GetHomeworks.Response.newBuilder()
                    .setPage(
                        ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                            .setTotalPages(assignments.getTotalPages())
                            .setTotalElements(assignments.getTotalElements())
                            .build())
                    .addAllHomeworks(snippets)
                    .build());
  }

  @Override
  public Homework retrieveHomeworkModel(CreateOrUpdateHomework.Request request) {
    return homeworkProtoConverter.retrieveModel(request);
  }
}
