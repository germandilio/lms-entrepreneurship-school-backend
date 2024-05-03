package ru.hse.lmsteam.backend.repository.impl.tasks;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.TestFilterOptions;

@Service
public class TestRepository extends AbstractTasksRepository<Test, UUID, TestFilterOptions> {
  public TestRepository(
      @Autowired MasterSlaveDbOperations db,
      @Autowired PlainSQLQueryTranslator<TestFilterOptions> filterOptionsQT) {
    super(db, filterOptionsQT);
  }

  public Flux<Test> findTasksByLesson(UUID lessonId) {
    if (lessonId == null) {
      return Flux.empty();
    }

    return db.slave.select(query(where("lesson_id").is(lessonId)), Test.class);
  }
}
