package ru.hse.lmsteam.backend.repository.query.translators;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.user.UserFilterOptions;

@Component
public class UserFilterOptionsQT extends AbstractSimpleQueryTranslator<UserFilterOptions> {
  private static final ImmutableMap<String, String> FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING =
      ImmutableMap.of(
          "name", "users.name",
          "email", "users.email",
          "role", "users.role");

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
        + withNonDeleted(withoutNonRetrievableRoles(getWhere(queryObject)))
        + getOrder(pageable.getSort(), FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING)
        + getLimitAndOffset(pageable);
  }

  public String translateToSql(
      UserFilterOptions queryObject, Pageable pageable, boolean retrieveSnippets) {
    if (!retrieveSnippets) return translateToSql(queryObject, pageable);
    else {
      if (queryObject == null) {
        throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
      }
      if (pageable == null) {
        throw new IllegalArgumentException(
            "Cannot translate queryObject to sql with null pageable!");
      }

      var selectBase = "SELECT users.id, users.name, users.surname, users.patronymic FROM users";
      return selectBase
          + withNonDeleted(withoutNonRetrievableRoles(getWhere(queryObject)))
          + getOrder(pageable.getSort(), FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING)
          + getLimitAndOffset(pageable);
    }
  }

  @Override
  public String translateToCountSql(UserFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }

    var selectBase = "SELECT COUNT(*) FROM users";
    return selectBase + withNonDeleted(withoutNonRetrievableRoles(getWhere(queryObject)));
  }

  private String withoutNonRetrievableRoles(String whereClause) {
    if (whereClause == null || whereClause.isEmpty()) {
      return " WHERE users.role != 'ADMIN' AND users.role != 'EXTERNAL_TEACHER'";
    } else {
      return whereClause + " AND users.role != 'ADMIN' AND users.role != 'EXTERNAL_TEACHER'";
    }
  }

  private String withNonDeleted(String whereClause) {
    if (whereClause == null || whereClause.isEmpty()) {
      return " WHERE users.is_deleted = false";
    } else {
      return whereClause + " AND users.is_deleted = false";
    }
  }

  @Override
  protected String buildWhereClause(UserFilterOptions queryObject) {
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
