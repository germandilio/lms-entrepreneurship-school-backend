package ru.hse.lmsteam.backend.repository.query.translators;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.UserFilterOptions;

@Component("userFilterOptionsQT")
public class UserFilterOptionsQueryTranslator implements SimpleQueryTranslator<UserFilterOptions> {

  @Override
  public String translateToSql(UserFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    var selectBase = "SELECT users.* FROM users LEFT JOIN groups ON users.group_id = groups.id";

    return selectBase
        + getWhere(queryObject)
        + getOrder(pageable.getSort())
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(UserFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }

    var selectBase = "SELECT COUNT(*) FROM users LEFT JOIN groups ON users.group_id = groups.id";
    return selectBase + getWhere(queryObject);
  }

  private String getWhere(UserFilterOptions queryObject) {
    var whereClause = buildWhereClause(queryObject);
    if (whereClause == null || whereClause.isEmpty()) {
      return "";
    } else {
      return " WHERE " + whereClause;
    }
  }

  private String buildWhereClause(UserFilterOptions queryObject) {
    var nameCriteria =
        Optional.ofNullable(queryObject.namePattern())
            .map(name -> "users.name LIKE '%" + name + "%'");
    var emailCriteria =
        Optional.ofNullable(queryObject.emailPattern())
            .map(email -> "users.email LIKE '%" + email + "%'");
    var groupNumberCriteria =
        Optional.ofNullable(queryObject.groupNumbers())
            .filter(groupNumbers -> !groupNumbers.isEmpty())
            .map(
                groupNumbers ->
                    "groups.number IN ("
                        + String.join(
                            ",", groupNumbers.stream().map(String::valueOf).toArray(String[]::new))
                        + ")");
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

  private String getOrder(Sort sort) {
    if (sort == null || sort.isEmpty()) {
      return "";
    }
    var orderClause = new StringBuilder(" ORDER BY ");
    sort.forEach(
        order -> {
          orderClause
              .append(order.getProperty())
              .append(" ")
              .append(order.getDirection())
              .append(", ");
        });
    // clear last comma
    orderClause.delete(orderClause.length() - 2, orderClause.length());
    return orderClause.toString();
  }

  private String getLimitAndOffset(Pageable pageable) {
    return " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
  }
}
