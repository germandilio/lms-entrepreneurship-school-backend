package ru.hse.lmsteam.backend.api.v1.controllers.tasks;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.TestApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.tasks.TestsControllerDocSchema;
import ru.hse.lmsteam.backend.service.model.tasks.TestFilterOptions;
import ru.hse.lmsteam.backend.service.tasks.TestManager;
import ru.hse.lmsteam.schema.api.tests.*;

@RestController
@RequestMapping(
    value = "/api/v1/tests",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class TestsController implements TestsControllerDocSchema {
  private final TestManager testManager;
  private final TestApiProtoBuilder testApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetTest.Response> getTest(@PathVariable UUID id) {
    return testManager.findById(id).map(testApiProtoBuilder::buildGetTestResponse);
  }

  @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateTest.Response> createTest(
      @RequestBody CreateOrUpdateTest.Request request) {
    var model = testApiProtoBuilder.retrieveTestModel(request);

    return testManager.create(model).map(testApiProtoBuilder::buildCreateTestResponse);
  }

  @PutMapping(
      path = "/{id}",
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
  @Override
  public Mono<CreateOrUpdateTest.Response> updateTest(
      @PathVariable UUID id, @RequestBody CreateOrUpdateTest.Request request) {
    var model = testApiProtoBuilder.retrieveTestModel(request).withId(id);
    return testManager.update(model).map(testApiProtoBuilder::buildUpdateTestResponse);
  }

  @DeleteMapping("/{id}")
  @Override
  public Mono<DeleteTest.Response> deleteTest(@PathVariable UUID id) {
    return testManager.delete(id).map(testApiProtoBuilder::buildDeleteTestResponse);
  }

  @PageableAsQueryParam
  @GetMapping("/list")
  @Override
  public Mono<GetTests.Response> getTests(
      @RequestParam(required = false) UUID lessonId,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) Instant deadlineFrom,
      @RequestParam(required = false) Instant deadlineTo,
      @RequestParam(required = false) Instant publishFrom,
      @RequestParam(required = false) Instant publishTo,
      Pageable pageable) {
    var options =
        new TestFilterOptions(title, lessonId, publishFrom, publishTo, deadlineFrom, deadlineTo);
    return testManager.findAll(options, pageable).map(testApiProtoBuilder::buildGetTestsResponse);
  }
}
