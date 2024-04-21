package ru.hse.lmsteam.backend.api.v1.schema;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.lessons.*;

public interface LessonsControllerDocSchema {
  Mono<GetLesson.Response> getLesson(UUID id);

  Mono<CreateLesson.Response> createLesson(CreateLesson.Request request);

  Mono<UpdateLesson.Response> updateLesson(UUID id, UpdateLesson.Request request);

  Mono<DeleteLesson.Response> deleteLesson(UUID id);

  Mono<GetLessons.Response> getLessons(
      Integer lessonNumber, String title, LocalDate publishDate, Pageable pageable);
}
