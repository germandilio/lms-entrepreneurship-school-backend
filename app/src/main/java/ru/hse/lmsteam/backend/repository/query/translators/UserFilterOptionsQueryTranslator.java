package ru.hse.lmsteam.backend.repository.query.translators;

import static org.springframework.data.relational.core.query.Criteria.where;

import java.util.Optional;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

@Component("userFilterOptionsQT")
public class UserFilterOptionsQueryTranslator implements QueryTranslator<UserFilterOptions> {

  @Override
  public Query translate(UserFilterOptions queryObject) {
    var nameCriteria =
        Optional.ofNullable(queryObject.namePattern())
            .map(where("name")::like)
            .orElse(Criteria.empty());
    var emailCriteria =
        Optional.ofNullable(queryObject.emailPattern())
            .map(where("email")::like)
            .orElse(Criteria.empty());
    var groupNumberCriteria =
        Optional.ofNullable(queryObject.groupNumbers())
            .map(where("group_number")::in)
            .orElse(Criteria.empty());
    var roleCriteria =
        Optional.ofNullable(queryObject.roles()).map(where("roles")::in).orElse(Criteria.empty());
    var isDeletedCriteria =
        Optional.ofNullable(queryObject.isDeleted())
            .map(where("is_deleted")::is)
            .orElse(Criteria.empty());

    return Query.query(
        nameCriteria
            .and(emailCriteria)
            .and(groupNumberCriteria)
            .and(roleCriteria)
            .and(isDeletedCriteria));
  }
}
