package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;
import ru.hse.lmsteam.schema.api.tests.DeleteTest;
import ru.hse.lmsteam.schema.api.tests.GetTest;
import ru.hse.lmsteam.schema.api.tests.GetTests;

@Component
@RequiredArgsConstructor
public class TestApiProtoBuilderImpl implements TestApiProtoBuilder {
  private final TestProtoConverter testProtoConverter;

  @Override
  public Mono<GetTest.Response> buildGetTestResponse(Test test) {
    return testProtoConverter
        .map(test)
        .singleOptional()
        .map(
            testOpt -> {
              var b = GetTest.Response.newBuilder();
              testOpt.ifPresent(b::setTest);
              return b.build();
            });
  }

  @Override
  public Mono<CreateOrUpdateTest.Response> buildCreateOrUpdateTestResponse(Test test) {
    return testProtoConverter
        .map(test)
        .singleOptional()
        .map(
            testOpt -> {
              var b = CreateOrUpdateTest.Response.newBuilder();
              testOpt.ifPresent(b::setTest);
              return b.build();
            });
  }

  @Override
  public DeleteTest.Response buildDeleteTestResponse(long itemsDeleted) {
    return DeleteTest.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public Mono<GetTests.Response> buildGetTestsResponse(Page<Test> tests) {
    return Flux.fromIterable(tests.toList())
        .flatMap(testProtoConverter::toSnippet)
        .collectList()
        .map(
            testSnippets -> {
              var b = GetTests.Response.newBuilder();
              b.setPage(
                  ru.hse.lmsteam.schema.api.common.Page.newBuilder()
                      .setTotalPages(tests.getTotalPages())
                      .setTotalElements(tests.getTotalElements())
                      .build());
              b.addAllTests(testSnippets);
              return b.build();
            });
  }

  @Override
  public Test retrieveTestModel(CreateOrUpdateTest.Request request) {
    return testProtoConverter.retrieveModel(request);
  }
}
