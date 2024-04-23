package ru.hse.lmsteam.backend.repository.impl.tasks;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.tasks.Exam;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.ExamFilterOptions;

@Repository
public class ExamRepository extends AbstractTasksRepository<Exam, UUID, ExamFilterOptions> {
  public ExamRepository(
      @Autowired MasterSlaveDbOperations db,
      @Autowired PlainSQLQueryTranslator<ExamFilterOptions> filterOptionsQT) {
    super(db, filterOptionsQT);
  }
}
