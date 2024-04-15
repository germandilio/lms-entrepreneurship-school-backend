package ru.hse.lmsteam.backend.api.v1.controllers;

import com.google.protobuf.InvalidProtocolBufferException;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.lesson.LessonsApiProtoConverter;
import ru.hse.lmsteam.backend.api.v1.schema.LessonsControllerDocSchema;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;
import ru.hse.lmsteam.schema.api.lessons.*;

@RestController
@RequestMapping(
    value = "/api/v1/lessons",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class LessonsController implements LessonsControllerDocSchema {
  private final LessonManager lessonManager;
  private final LessonsApiProtoConverter lessonsApiProtoConverter;

  @GetMapping("/{id}")
  @Override
  public Mono<GetLesson.Response> getLesson(@PathVariable UUID id) {
    return lessonManager
        .findById(id)
        .handle(
            (lesson, sink) -> {
              try {
                sink.next(
                    GetLesson.Response.newBuilder()
                        .setLesson(lessonsApiProtoConverter.map(lesson))
                        .build());
              } catch (InvalidProtocolBufferException e) {
                sink.error(new RuntimeException(e));
              }
            });
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateLesson.Response> createLesson(@RequestBody CreateLesson.Request request) {
    return lessonManager
        .create(lessonsApiProtoConverter.retrieveCreateModel(request))
        .handle(
            (lesson, sink) -> {
              try {
                sink.next(
                    CreateLesson.Response.newBuilder()
                        .setLesson(lessonsApiProtoConverter.map(lesson))
                        .build());
              } catch (InvalidProtocolBufferException e) {
                sink.error(new RuntimeException(e));
              }
            });
  }

  @PutMapping(
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE},
      path = "/{id}")
  @Override
  public Mono<UpdateLesson.Response> updateLesson(
      @PathVariable UUID id, @RequestBody UpdateLesson.Request request) {
    return lessonManager
        .update(lessonsApiProtoConverter.map(id, request.getLesson()))
        .handle(
            (lesson, sink) -> {
              try {
                sink.next(
                    UpdateLesson.Response.newBuilder()
                        .setLesson(lessonsApiProtoConverter.map(lesson))
                        .build());
              } catch (InvalidProtocolBufferException e) {
                sink.error(new RuntimeException(e));
              }
            });
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteLesson.Response> deleteLesson(@PathVariable UUID id) {
    return lessonManager
        .delete(id)
        .map(count -> DeleteLesson.Response.newBuilder().setEntitiesDeleted(count).build());
  }

  @GetMapping("/list")
  @PageableAsQueryParam
  @Override
  public Mono<GetLessons.Response> getLessons(
      @RequestParam(required = false) Integer lessonNumber,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) LocalDate publishDate,
      Pageable pageable) {
    var filterOptions =
        LessonsFilterOptions.builder()
            .lessonNumber(lessonNumber)
            .title(title)
            .publishDate(publishDate)
            .build();
    return lessonManager
        .findAll(filterOptions, pageable)
        .map(lessonsApiProtoConverter::buildGetLessonsResponse);
  }
}
