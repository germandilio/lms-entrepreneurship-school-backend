package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.repository.SubmissionRepository;
import ru.hse.lmsteam.backend.repository.impl.tasks.TestRepository;

@Service
@RequiredArgsConstructor
public class TestDeleteManagerImpl implements TestDeleteManager {
  private final SubmissionRepository submissionRepository;
  private final TestRepository testRepository;

  @Transactional
  @Override
  public Mono<Long> deleteAllByLessonId(UUID lessonId) {
    if (lessonId == null) {
      return Mono.just(0L);
    }
    return testRepository
        .findTasksByLesson(lessonId)
        .collectList()
        .flatMap(
            tests -> {
              if (tests.isEmpty()) {
                return Mono.just(0L);
              }
              return submissionRepository.deleteAllByTaskIds(tests.stream().map(Test::id).toList());
            })
        .then(testRepository.deleteAllByLessonId(lessonId));
  }
}
