package ru.hse.lmsteam.backend.repository.impl.tasks;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
}
