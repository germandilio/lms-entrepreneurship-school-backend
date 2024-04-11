package ru.hse.lmsteam.backend.repository.query.translators;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class AbstractSimpleQueryTranslator<T> implements PlainSQLQueryTranslator<T> {
  protected abstract String getWhere(T queryObject);

  protected String getOrder(Sort sort) {
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

  protected String getLimitAndOffset(Pageable pageable) {
    return " LIMIT " + pageable.getPageSize() + " OFFSET " + pageable.getOffset();
  }
}
