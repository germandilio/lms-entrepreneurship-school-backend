package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface TestDeleteManager {
  Mono<Long> deleteAllByLessonId(UUID lessonId);
}
