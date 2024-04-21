package ru.hse.lmsteam.backend.repository.query.translators;

import static org.springframework.data.relational.core.query.Criteria.where;

import java.util.Optional;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.teams.TeamsFilterOptions;

@Component
public class TeamFilterOptionsQT implements QueryTranslator<TeamsFilterOptions> {
  @Override
  public Query translate(TeamsFilterOptions queryObject) {
    var numberCriteria =
        Optional.ofNullable(queryObject.number()).map(where("number")::is).orElse(Criteria.empty());

    return Query.query(numberCriteria.and(Criteria.where("is_deleted").isFalse()));
  }
}
