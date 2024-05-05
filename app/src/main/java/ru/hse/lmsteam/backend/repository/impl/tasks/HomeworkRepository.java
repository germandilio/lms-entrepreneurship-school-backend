package ru.hse.lmsteam.backend.repository.impl.tasks;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.tasks.Homework;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

@Repository
public class HomeworkRepository
    extends AbstractTasksRepository<Homework, UUID, HomeworkFilterOptions> {
  public HomeworkRepository(
      @Autowired MasterSlaveDbOperations db,
      @Autowired PlainSQLQueryTranslator<HomeworkFilterOptions> filterOptionsQT) {
    super(db, filterOptionsQT);
  }

  public Flux<Homework> findTasksByLesson(UUID lessonId) {
    if (lessonId == null) {
      return Flux.empty();
    }

    return db.slave.select(query(where("lesson_id").is(lessonId)), Homework.class);
  }

  public Mono<Long> deleteAllByLessonId(UUID lessonId) {
    if (lessonId == null) {
      return Mono.just(0L);
    }

    return db.master.delete(query(where("lesson_id").is(lessonId)), Homework.class);
  }
}
