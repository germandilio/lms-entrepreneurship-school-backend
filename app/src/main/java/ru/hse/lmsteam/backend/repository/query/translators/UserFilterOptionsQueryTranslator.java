package ru.hse.lmsteam.backend.repository.query.translators;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;

@Component("userFilterOptionsQT")
public class UserFilterOptionsQueryTranslator
    extends AbstractSimpleQueryTranslator<UserFilterOptions> {

  @Override
  public String translateToSql(UserFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    var selectBase = "SELECT users.* FROM users";

    return selectBase
        + withNonDeleted(withAdminNonRetrievable(getWhere(queryObject)))
        + getOrder(pageable.getSort())
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(UserFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }

    var selectBase = "SELECT COUNT(*) FROM users";
    return selectBase + withNonDeleted(withAdminNonRetrievable(getWhere(queryObject)));
  }

  @Override
  protected String getWhere(UserFilterOptions queryObject) {
    var whereClause = buildWhereClause(queryObject);
    if (whereClause == null || whereClause.isEmpty()) {
      return "";
    } else {
      return " WHERE " + whereClause;
    }
  }

  private String withAdminNonRetrievable(String whereClause) {
    if (whereClause == null || whereClause.isEmpty()) {
      return " WHERE users.role != 'ADMIN'";
    } else {
      return whereClause + " AND users.role != 'ADMIN'";
    }
  }

  private String withNonDeleted(String whereClause) {
    if (whereClause == null || whereClause.isEmpty()) {
      return " WHERE users.is_deleted = false";
    } else {
      return whereClause + " AND users.is_deleted = false";
    }
  }

  private String buildWhereClause(UserFilterOptions queryObject) {
    var nameCriteria =
        Optional.ofNullable(queryObject.namePattern())
            .map(name -> "users.name ILIKE '%" + name + "%'");
    var emailCriteria =
        Optional.ofNullable(queryObject.emailPattern())
            .map(email -> "users.email ILIKE '%" + email + "%'");
    var groupNumberCriteria =
        Optional.ofNullable(queryObject.groupNumbers())
            .filter(groupNumbers -> !groupNumbers.isEmpty())
            .map(
                groupNumbers ->
                    "users.id IN ("
                        + "SELECT user_id FROM users_groups LEFT JOIN groups ON users_groups.group_id = groups.id WHERE groups.number IN ("
                        + String.join(
                            ",", groupNumbers.stream().map(String::valueOf).toArray(String[]::new))
                        + "))");
    var roleCriteria =
        Optional.ofNullable(queryObject.roles())
            .filter(roles -> !roles.isEmpty())
            .map(
                roles ->
                    "users.role IN ("
                        + String.join(
                            ",",
                            roles.stream().map(role -> "'" + role + "'").toArray(String[]::new))
                        + ")");
    var isDeletedCriteria =
        Optional.ofNullable(queryObject.isDeleted())
            .map(isDeleted -> "users.is_deleted = " + isDeleted);

    return Stream.of(
            nameCriteria, emailCriteria, groupNumberCriteria, roleCriteria, isDeletedCriteria)
        .flatMap(Optional::stream)
        .collect(Collectors.joining(" AND "));
  }
}
