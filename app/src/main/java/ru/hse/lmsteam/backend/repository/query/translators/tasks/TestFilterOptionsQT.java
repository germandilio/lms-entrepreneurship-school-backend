package ru.hse.lmsteam.backend.repository.query.translators.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.repository.query.translators.AbstractSimpleQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.TestFilterOptions;

@Component
public class TestFilterOptionsQT extends AbstractSimpleQueryTranslator<TestFilterOptions> {
  private static final ImmutableMap<String, String> FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING =
      ImmutableMap.of(
          "title", "tests.title",
          "publishDate", "tests.publish_date",
          "deadlineDate", "tests.deadline_date");

  @Override
  public String translateToSql(TestFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    return "SELECT * FROM tests"
        + getWhere(queryObject)
        + getOrder(pageable.getSort(), FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING)
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(TestFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    return "SELECT COUNT(*) FROM tests" + getWhere(queryObject);
  }

  @Override
  protected String buildWhereClause(TestFilterOptions queryObject) {
    var titleCriteria =
        Optional.ofNullable(queryObject.title()).map(title -> " title ILIKE '%" + title + "%'");
    var lessonIdCriteria =
        Optional.ofNullable(queryObject.lessonId())
            .map(lessonId -> " lesson_id = '" + lessonId + "'");

    var publishDateCriteria =
        getTimestampRangeClause(
            queryObject.publishDateFrom(), queryObject.publishDateTo(), "publish_date");
    var deadlineCriteria =
        getTimestampRangeClause(
            queryObject.deadlineFrom(), queryObject.deadlineTo(), "deadline_date");

    return Stream.of(titleCriteria, lessonIdCriteria, publishDateCriteria, deadlineCriteria)
        .flatMap(Optional::stream)
        .collect(java.util.stream.Collectors.joining(" AND "));
  }
}
