package ru.hse.lmsteam.backend.api.v1.schema;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.homeworks.*;

public interface HomeworksControllerSchemaDoc {
  Mono<GetHomework.Response> getHomework(UUID id);

  Mono<CreateOrUpdateHomework.Response> createHomework(CreateOrUpdateHomework.Request request);

  Mono<CreateOrUpdateHomework.Response> updateHomework(
      UUID id, CreateOrUpdateHomework.Request request);

  Mono<DeleteHomework.Response> deleteHomework(UUID id);

  Mono<GetHomeworks.Response> getHomeworks(
      UUID lessonId,
      String title,
      Instant deadlineFrom,
      Instant deadlineTo,
      Instant publishFrom,
      Instant publishTo,
      Boolean isGroup,
      Pageable pageable);
}
