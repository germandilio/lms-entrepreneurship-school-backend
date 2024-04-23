package ru.hse.lmsteam.backend.api.v1.controllers.tasks;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.ExamApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.tasks.ExamsControllerDocSchema;
import ru.hse.lmsteam.backend.service.model.tasks.ExamFilterOptions;
import ru.hse.lmsteam.backend.service.tasks.ExamManager;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;
import ru.hse.lmsteam.schema.api.exams.DeleteExam;
import ru.hse.lmsteam.schema.api.exams.GetExam;
import ru.hse.lmsteam.schema.api.exams.GetExams;

@RestController
@RequestMapping(
    value = "/api/v1/exams",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class ExamsController implements ExamsControllerDocSchema {
  private final ExamManager examManager;
  private final ExamApiProtoBuilder examApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetExam.Response> getExam(@PathVariable UUID id) {
    return examManager.findById(id).map(examApiProtoBuilder::buildGetExamResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateExam.Response> createExam(
      @RequestBody CreateOrUpdateExam.Request request) {
    var model = examApiProtoBuilder.retrieveExamModel(request);

    return examManager.create(model).map(examApiProtoBuilder::buildCreateOrUpdateExamResponse);
  }

  @PutMapping(
      path = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateExam.Response> updateExam(
      @PathVariable UUID id, @RequestBody CreateOrUpdateExam.Request request) {
    var model = examApiProtoBuilder.retrieveExamModel(request).withId(id);
    return examManager.update(model).map(examApiProtoBuilder::buildCreateOrUpdateExamResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteExam.Response> deleteExam(@PathVariable UUID id) {
    return examManager.delete(id).map(examApiProtoBuilder::buildDeleteExamResponse);
  }

  @PageableAsQueryParam
  @GetMapping("/list")
  @Override
  public Mono<GetExams.Response> getExams(
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Instant deadlineFrom,
      @RequestParam(required = false) Instant deadlineTo,
      @RequestParam(required = false) Instant publishFrom,
      @RequestParam(required = false) Instant publishTo,
      Pageable pageable) {
    var options = new ExamFilterOptions(title, publishFrom, publishTo, deadlineFrom, deadlineTo);
    return examManager.findAll(options, pageable).map(examApiProtoBuilder::buildGetExamsResponse);
  }
}
