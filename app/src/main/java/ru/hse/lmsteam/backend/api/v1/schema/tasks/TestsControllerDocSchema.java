package ru.hse.lmsteam.backend.api.v1.schema.tasks;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;
import ru.hse.lmsteam.schema.api.tests.DeleteTest;
import ru.hse.lmsteam.schema.api.tests.GetTest;
import ru.hse.lmsteam.schema.api.tests.GetTests;

public interface TestsControllerDocSchema {
  Mono<GetTest.Response> getTest(UUID id);

  Mono<CreateOrUpdateTest.Response> createTest(CreateOrUpdateTest.Request request);

  Mono<CreateOrUpdateTest.Response> updateTest(UUID id, CreateOrUpdateTest.Request request);

  Mono<DeleteTest.Response> deleteTest(UUID id);

  Mono<GetTests.Response> getTests(
      UUID lessonId,
      String title,
      Instant deadlineFrom,
      Instant deadlineTo,
      Instant publishFrom,
      Instant publishTo,
      Pageable pageable);
}
