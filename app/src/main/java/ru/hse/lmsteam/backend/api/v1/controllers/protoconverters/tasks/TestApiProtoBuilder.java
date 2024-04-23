package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;
import ru.hse.lmsteam.schema.api.tests.DeleteTest;
import ru.hse.lmsteam.schema.api.tests.GetTest;
import ru.hse.lmsteam.schema.api.tests.GetTests;

public interface TestApiProtoBuilder {
  Mono<GetTest.Response> buildGetTestResponse(Test test);

  Mono<CreateOrUpdateTest.Response> buildCreateOrUpdateTestResponse(Test test);

  DeleteTest.Response buildDeleteTestResponse(long itemsDeleted);

  Mono<GetTests.Response> buildGetTestsResponse(Page<Test> tests);

  Test retrieveTestModel(CreateOrUpdateTest.Request request);
}
