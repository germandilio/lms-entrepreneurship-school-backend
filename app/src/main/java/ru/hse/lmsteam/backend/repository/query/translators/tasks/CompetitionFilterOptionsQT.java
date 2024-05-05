package ru.hse.lmsteam.backend.repository.query.translators.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.repository.query.translators.AbstractSimpleQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.CompetitionFilterOptions;

@Component
public class CompetitionFilterOptionsQT
    extends AbstractSimpleQueryTranslator<CompetitionFilterOptions> {
  private final ImmutableMap<String, String> FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING =
      ImmutableMap.of(
          "title", "competitions.title",
          "publishDate", "competitions.publish_date",
          "deadlineDate", "competitions.deadline_date");

  @Override
  public String translateToSql(CompetitionFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    return "SELECT * FROM competitions"
        + getWhere(queryObject)
        + getOrder(pageable.getSort(), FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING)
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(CompetitionFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    return "SELECT COUNT(*) FROM competitions" + getWhere(queryObject);
  }

  @Override
  protected String buildWhereClause(CompetitionFilterOptions queryObject) {
    var titleCriteria =
        Optional.ofNullable(queryObject.title()).map(title -> " title ILIKE '%" + title + "%'");

    var publishDateCriteria =
        getTimestampRangeClause(
            queryObject.publishDateFrom(), queryObject.publishDateTo(), "publish_date");
    var deadlineCriteria =
        getTimestampRangeClause(
            queryObject.deadlineFrom(), queryObject.deadlineTo(), "deadline_date");

    return Stream.of(titleCriteria, publishDateCriteria, deadlineCriteria)
        .flatMap(Optional::stream)
        .collect(java.util.stream.Collectors.joining(" AND "));
  }
}
