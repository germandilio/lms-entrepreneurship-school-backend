package ru.hse.lmsteam.backend.api.v1.schema.tasks;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.exams.CreateOrUpdateExam;
import ru.hse.lmsteam.schema.api.exams.DeleteExam;
import ru.hse.lmsteam.schema.api.exams.GetExam;
import ru.hse.lmsteam.schema.api.exams.GetExams;

public interface ExamsControllerDocSchema {
  Mono<GetExam.Response> getExam(UUID id);

  Mono<CreateOrUpdateExam.Response> createExam(CreateOrUpdateExam.Request request);

  Mono<CreateOrUpdateExam.Response> updateExam(UUID id, CreateOrUpdateExam.Request request);

  Mono<DeleteExam.Response> deleteExam(UUID id);

  Mono<GetExams.Response> getExams(
      String title,
      Instant deadlineFrom,
      Instant deadlineTo,
      Instant publishFrom,
      Instant publishTo,
      Pageable pageable);
}
