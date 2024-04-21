package ru.hse.lmsteam.backend.api.v1.controllers.tasks;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.HomeworkApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.tasks.HomeworksControllerDocSchema;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;
import ru.hse.lmsteam.backend.service.tasks.HomeworkManager;
import ru.hse.lmsteam.schema.api.homeworks.*;

@RestController
@RequestMapping(
    value = "/api/v1/homeworks",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class HomeworksController implements HomeworksControllerDocSchema {
  private final HomeworkManager homeworkManager;
  private final HomeworkApiProtoBuilder homeworkApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetHomework.Response> getHomework(@PathVariable UUID id) {
    return homeworkManager.findById(id).map(homeworkApiProtoBuilder::buildGetHomeworkResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateHomework.Response> createHomework(
      @RequestBody CreateOrUpdateHomework.Request request) {
    var model = homeworkApiProtoBuilder.retrieveHomeworkModel(request);

    return homeworkManager.create(model).map(homeworkApiProtoBuilder::buildCreateHomeworkResponse);
  }

  @PutMapping(
      path = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateHomework.Response> updateHomework(
      @PathVariable UUID id, @RequestBody CreateOrUpdateHomework.Request request) {
    var model = homeworkApiProtoBuilder.retrieveHomeworkModel(request).withId(id);
    return homeworkManager.update(model).map(homeworkApiProtoBuilder::buildUpdateHomeworkResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteHomework.Response> deleteHomework(@PathVariable UUID id) {
    return homeworkManager.delete(id).map(homeworkApiProtoBuilder::buildDeleteHomeworkResponse);
  }

  @PageableAsQueryParam
  @GetMapping("/list")
  @Override
  public Mono<GetHomeworks.Response> getHomeworks(
      @RequestParam(required = false) UUID lessonId,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Instant deadlineFrom,
      @RequestParam(required = false) Instant deadlineTo,
      @RequestParam(required = false) Instant publishFrom,
      @RequestParam(required = false) Instant publishTo,
      @RequestParam(required = false) Boolean isGroup,
      Pageable pageable) {
    var options =
        new HomeworkFilterOptions(
            title, lessonId, publishFrom, publishTo, deadlineFrom, deadlineTo, isGroup);
    return homeworkManager
        .findAll(options, pageable)
        .map(homeworkApiProtoBuilder::buildGetHomeworksResponse);
  }
}
