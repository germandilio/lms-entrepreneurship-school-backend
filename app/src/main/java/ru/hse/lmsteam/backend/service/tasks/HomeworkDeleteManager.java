package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import reactor.core.publisher.Mono;

public interface HomeworkDeleteManager {

  Mono<Long> deleteAllByLessonId(UUID lessonId);
}
