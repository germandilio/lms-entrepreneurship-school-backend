package ru.hse.lmsteam.backend.repository.query.translators;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.lessons.LessonsFilterOptions;

@Component
public class LessonsFilterOptionsQT extends AbstractSimpleQueryTranslator<LessonsFilterOptions> {
  private static final ImmutableMap<String, String> FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING =
      ImmutableMap.of(
          "title", "lessons.title",
          "lessonNumber", "lessons.lesson_number",
          "publishDate", "lessons.publish_date");

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
        + getOrder(pageable.getSort(), FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING)
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(LessonsFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }

    return "SELECT COUNT(*) FROM lessons" + getWhere(queryObject);
  }

  @Override
  protected String buildWhereClause(LessonsFilterOptions options) {
    var titleCriteria =
        Optional.ofNullable(options.title()).map(title -> "lessons.title ILIKE '%" + title + "%'");
    var numberCriteria =
        Optional.ofNullable(options.lessonNumber())
            .map(number -> "lessons.lesson_number = " + number);
    var publishDateCriteria =
        getRangeClause(
            options.publishDateFrom(), options.publishDateTimeTo(), "lessons.publish_date");

    return Stream.of(titleCriteria, numberCriteria, publishDateCriteria)
        .flatMap(Optional::stream)
        .collect(Collectors.joining(" AND "));
  }
}
