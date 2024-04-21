package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
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
  public GetTest.Response buildGetTestResponse(Test test) {
    var b = GetTest.Response.newBuilder();
    if (test != null) {
      b.setTest(testProtoConverter.map(test));
    }
    return b.build();
  }

  @Override
  public CreateOrUpdateTest.Response buildCreateTestResponse(Test test) {
    var b = CreateOrUpdateTest.Response.newBuilder();
    if (test != null) {
      b.setTest(testProtoConverter.map(test));
    }
    return b.build();
  }

  @Override
  public CreateOrUpdateTest.Response buildUpdateTestResponse(Test test) {
    var b = CreateOrUpdateTest.Response.newBuilder();
    if (test != null) {
      b.setTest(testProtoConverter.map(test));
    }
    return b.build();
  }

  @Override
  public DeleteTest.Response buildDeleteTestResponse(long itemsDeleted) {
    return DeleteTest.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public GetTests.Response buildGetTestsResponse(Page<Test> tests) {
    var b = GetTests.Response.newBuilder();
    b.setPage(
        ru.hse.lmsteam.schema.api.common.Page.newBuilder()
            .setTotalPages(tests.getTotalPages())
            .setTotalElements(tests.getTotalElements())
            .build());
    b.addAllTests(tests.map(testProtoConverter::toSnippet));
    return b.build();
  }

  @Override
  public Test retrieveTestModel(CreateOrUpdateTest.Request request) {
    return testProtoConverter.retrieveModel(request);
  }
}
