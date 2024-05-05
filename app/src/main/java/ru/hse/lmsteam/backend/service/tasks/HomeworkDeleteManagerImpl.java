package ru.hse.lmsteam.backend.service.tasks;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.repository.SubmissionRepository;
import ru.hse.lmsteam.backend.repository.impl.tasks.HomeworkRepository;

@Service
@RequiredArgsConstructor
public class HomeworkDeleteManagerImpl implements HomeworkDeleteManager {
  private final HomeworkRepository homeworkRepository;
  private final SubmissionRepository submissionRepository;

  @Transactional
  @Override
  public Mono<Long> deleteAllByLessonId(UUID lessonId) {
    if (lessonId == null) {
      return Mono.just(0L);
    }
    return homeworkRepository
        .findTasksByLesson(lessonId)
        .collectList()
        .flatMap(
            homeworks -> {
              if (homeworks.isEmpty()) {
                return Mono.just(0L);
              }
              return submissionRepository.deleteAllByTaskIds(
                  homeworks.stream().map(Homework::id).toList());
            })
        .then(homeworkRepository.deleteAllByLessonId(lessonId));
  }
}
