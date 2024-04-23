package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;

public interface TestProtoConverter {
  Mono<ru.hse.lmsteam.schema.api.tests.Test> map(Test test);

  Test map(ru.hse.lmsteam.schema.api.tests.Test test);

  Mono<ru.hse.lmsteam.schema.api.tests.TestSnippet> toSnippet(Test test);

  Test retrieveModel(CreateOrUpdateTest.Request test);
}
