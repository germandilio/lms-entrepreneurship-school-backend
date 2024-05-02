package ru.hse.lmsteam.backend.api.v1.controllers;

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
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;
import ru.hse.lmsteam.schema.api.lessons.*;

@RestController
@RequestMapping(
    value = "/api/v1/lessons",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class LessonsController implements LessonsControllerDocSchema {
  private final LessonManager lessonManager;
  private final LessonsApiProtoConverter lessonsApiProtoConverter;

  @GetMapping("/{id}")
  @Override
  public Mono<GetLesson.Response> getLesson(@PathVariable UUID id) {
    return lessonManager
        .findById(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Lesson not found.")))
        .map(lessonsApiProtoConverter::buildGetLessonResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateLesson.Response> createLesson(
      @RequestBody CreateOrUpdateLesson.Request request) {
    return lessonManager
        .create(lessonsApiProtoConverter.retrieveCreateModel(request))
        .map(
            lesson ->
                CreateOrUpdateLesson.Response.newBuilder()
                    .setLesson(lessonsApiProtoConverter.map(lesson))
                    .build());
  }

  @PutMapping(
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE},
      path = "/{id}")
  @Override
  public Mono<CreateOrUpdateLesson.Response> updateLesson(
      @PathVariable UUID id, @RequestBody CreateOrUpdateLesson.Request request) {
    var lesson = lessonsApiProtoConverter.retrieveCreateModel(request).withId(id);
    return lessonManager
        .update(lesson)
        .map(
            lessonResponse ->
                CreateOrUpdateLesson.Response.newBuilder()
                    .setLesson(lessonsApiProtoConverter.map(lessonResponse))
                    .build());
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteLesson.Response> deleteLesson(@PathVariable UUID id) {
    return lessonManager
        .delete(id)
        .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Lesson not found.")))
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
