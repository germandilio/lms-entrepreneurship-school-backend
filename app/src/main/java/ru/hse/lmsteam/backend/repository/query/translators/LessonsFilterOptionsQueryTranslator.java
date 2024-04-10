package ru.hse.lmsteam.backend.repository.query.translators;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

@Component("lessonsFilterOptionsQT")
public class LessonsFilterOptionsQueryTranslator
    implements SimpleQueryTranslator<LessonsFilterOptions> {
  @Override
  public String translateToSql(LessonsFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    var selectBase = "SELECT lessons.* FROM lessons";
    return selectBase
        + getWhere(queryObject)
        + getOrder(pageable.getSort())
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(LessonsFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }

    return "SELECT COUNT(*) FROM lessons" + getWhere(queryObject);
  }

  private String getWhere(LessonsFilterOptions queryObject) {
    var whereClause = buildWhereClause(queryObject);
    if (whereClause == null || whereClause.isEmpty()) {
      return "";
    } else {
      return " WHERE " + whereClause;
    }
  }

  private String buildWhereClause(LessonsFilterOptions options) {
    var titleCriteria =
        Optional.ofNullable(options.title()).map(title -> "lessons.title ILIKE '%" + title + "%'");
    var numberCriteria =
        Optional.ofNullable(options.lessonNumber())
            .map(number -> "lessons.lesson_number = " + number);
    var publishDateCriteria =
        Optional.ofNullable(options.publishDate())
            .map(date -> "lessons.publish_date = '" + date + "'");

    return Stream.of(titleCriteria, numberCriteria, publishDateCriteria)
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
