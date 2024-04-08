package ru.hse.lmsteam.backend.repository.query.translators;

import static org.springframework.data.relational.core.query.Criteria.where;

import java.util.Optional;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.groups.GroupsFilterOptions;

@Component("groupFilterOptionsQT")
public class GroupFilterOptionsQueryTranslator implements QueryTranslator<GroupsFilterOptions> {
  @Override
  public Query translate(GroupsFilterOptions queryObject) {
    var numberCriteria =
        Optional.ofNullable(queryObject.number()).map(where("number")::is).orElse(Criteria.empty());

    return Query.query(numberCriteria.and(Criteria.where("is_deleted").isFalse()));
  }
}
