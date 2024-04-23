package ru.hse.lmsteam.backend.repository.impl.tasks;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.hse.lmsteam.backend.config.persistence.MasterSlaveDbOperations;
import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.backend.repository.query.translators.PlainSQLQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.CompetitionFilterOptions;

@Repository
public class CompetitionRepository
    extends AbstractTasksRepository<Competition, UUID, CompetitionFilterOptions> {
  public CompetitionRepository(
      @Autowired MasterSlaveDbOperations db,
      @Autowired PlainSQLQueryTranslator<CompetitionFilterOptions> filterOptionsQT) {
    super(db, filterOptionsQT);
  }
}
