package ru.hse.lmsteam.backend.repository.impl.tasks;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
}
