package ru.hse.lmsteam.backend.api.v1.schema;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.assignments.*;

public interface HomeAssignmentsControllerSchemaDoc {
  Mono<GetHomeAssigment.Response> getHomeAssignment(UUID id);

  Mono<CreateHomeAssignment.Response> createHomeAssignment(CreateHomeAssignment.Request request);

  Mono<UpdateHomeAssignment.Response> updateHomeAssignment(UpdateHomeAssignment.Request request);

  Mono<DeleteHomeAssignment.Response> deleteHomeAssignment(UUID id);

  Mono<GetHomeAssignments.Response> getHomeAssignments(
      UUID lessonId,
      String title,
      Instant deadlineFrom,
      Instant deadlineTo,
      Instant publishFrom,
      Instant publishTo,
      Pageable pageable);
}
