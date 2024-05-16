package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.service.lesson.LessonManager;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;
import ru.hse.lmsteam.schema.api.tests.DeleteTest;
import ru.hse.lmsteam.schema.api.tests.GetTest;
import ru.hse.lmsteam.schema.api.tests.GetTests;

@Component
@RequiredArgsConstructor
public class TestApiProtoBuilderImpl implements TestApiProtoBuilder {
  private final TestProtoConverter testProtoConverter;
  private final LessonManager lessonManager;

  @Override
  public Mono<GetTest.Response> buildGetTestResponse(Test test) {
    return lessonManager
        .findById(test.lessonId())
        .singleOptional()
        .map(
            lessonOpt -> {
              var b = GetTest.Response.newBuilder();
              lessonOpt.ifPresent(l -> b.setTest(testProtoConverter.map(test, l)));
              return b.build();
            });
  }

  @Override
  public Mono<CreateOrUpdateTest.Response> buildCreateOrUpdateTestResponse(Test test) {
    return lessonManager
        .findById(test.lessonId())
        .singleOptional()
        .map(
            lessonOpt -> {
              var b = CreateOrUpdateTest.Response.newBuilder();
              lessonOpt.ifPresent(l -> b.setTest(testProtoConverter.map(test, l)));
              return b.build();
            });
  }

  @Override
  public DeleteTest.Response buildDeleteTestResponse(long itemsDeleted) {
    return DeleteTest.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public Mono<GetTests.Response> buildGetTestsResponse(Page<Test> tests) {
    var lessonF =
        lessonManager.findByIds(tests.stream().map(Test::lessonId).collect(Collectors.toSet()));

    return lessonF.map(
        lessons -> {
          var testSnippets =
              tests.stream()
                  .map(hw -> testProtoConverter.toSnippet(hw, lessons.get(hw.lessonId())))
                  .toList();
          return GetTests.Response.newBuilder()
              .setPage(
                  ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                      .setTotalPages(tests.getTotalPages())
                      .setTotalElements(tests.getTotalElements())
                      .build())
              .addAllTests(testSnippets)
              .build();
        });
  }

  @Override
  public Test retrieveTestModel(CreateOrUpdateTest.Request request) {
    return testProtoConverter.retrieveModel(request);
  }
}
