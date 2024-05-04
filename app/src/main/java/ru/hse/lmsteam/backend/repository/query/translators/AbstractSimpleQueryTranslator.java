package ru.hse.lmsteam.backend.repository.query.translators;

import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicExpectationFailedException;

public abstract class AbstractSimpleQueryTranslator<T> implements PlainSQLQueryTranslator<T> {
  protected abstract String buildWhereClause(T queryObject);

  protected String getWhere(T queryObject) {
    var whereClause = buildWhereClause(queryObject);
    if (whereClause == null || whereClause.isEmpty()) {
      return "";
    } else {
      return " WHERE " + whereClause;
    }
  }

  /**
   * Util method to get sql order clause.
   *
   * @param sort Spring sort default object
   * @return order clause string
   */
  protected String getOrder(
      Sort sort, ImmutableMap<String, String> filterPropertyToDbColumnsMapping) {
    if (sort == null || sort.isEmpty()) {
      return "";
    }
    var orderClause = new StringBuilder(" ORDER BY ");
    sort.forEach(
        order -> {
          var dbColumn = filterPropertyToDbColumnsMapping.get(order.getProperty());
          if (dbColumn == null) {
            throw new BusinessLogicExpectationFailedException(
                "Unknown sort property: " + order.getProperty());
          } else {
            orderClause
                .append(order.getProperty())
                .append(" ")
                .append(order.getDirection())
                .append(", ");
          }
        });
    // clear last comma
    orderClause.delete(orderClause.length() - 2, orderClause.length());
    return orderClause.toString();
  }

  /**
   * Util method to get sql limit and offset clause.
   *
   * @param pageable Spring pageable default object
   * @return limit and offset clause string
   */
  protected String getLimitAndOffset(Pageable pageable) {
    return " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
  }

  /**
   * Util method to get sql timestamp range clause.
   *
   * @param from lowerBound of time range (exclusive). Pass null if unbounded from bottom.
   * @param to upperBound of time range (exclusive) Pass null if unbounded from above.
   * @param columnName sql column name
   * @return Optional of timestamp range clause, empty if both 'from' and 'to' are empty.
   */
  protected Optional<String> getTimestampRangeClause(Instant from, Instant to, String columnName) {
    if (columnName == null) {
      throw new IllegalArgumentException("Column name cannot be null to build time range clause!");
    }
    if (from != null && to != null) {
      return Optional.of(
          String.format(
              " %s IS NOT NULL AND %s BETWEEN '%s' AND '%s'", columnName, columnName, from, to));
    } else if (from != null) {
      return Optional.of(
          String.format(" %s IS NOT NULL AND %s >= '%s'", columnName, columnName, from));
    } else if (to != null) {
      return Optional.of(
          String.format(" %s IS NOT NULL AND %s <= '%s'", columnName, columnName, to));
    } else {
      return Optional.empty();
    }
  }
}
