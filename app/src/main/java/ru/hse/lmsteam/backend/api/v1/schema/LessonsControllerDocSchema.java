package ru.hse.lmsteam.backend.api.v1.schema;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.lessons.*;

public interface LessonsControllerDocSchema {
  Mono<GetLesson.Response> getLesson(UUID id);

  Mono<CreateOrUpdateLesson.Response> createLesson(CreateOrUpdateLesson.Request request);

  Mono<CreateOrUpdateLesson.Response> updateLesson(UUID id, CreateOrUpdateLesson.Request request);

  Mono<DeleteLesson.Response> deleteLesson(UUID id);

  Mono<GetLessons.Response> getLessons(
      Integer lessonNumber,
      String title,
      Instant publishDateFrom,
      Instant publishDateTo,
      Pageable pageable);
}
