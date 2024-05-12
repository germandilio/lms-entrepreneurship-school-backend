package ru.hse.lmsteam.backend.repository.query.translators.tasks;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.repository.query.translators.AbstractSimpleQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

@Component
public class HomeworkFilterOptionsQT extends AbstractSimpleQueryTranslator<HomeworkFilterOptions> {
  private static final ImmutableMap<String, String> FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING =
      ImmutableMap.of(
          "title", "homeworks.title",
          "publishDate", "homeworks.publish_date",
          "deadlineDate", "homeworks.deadline_date",
          "isGroupWork", "homeworks.is_group");

  @Override
  public String translateToSql(HomeworkFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    return "SELECT * FROM homeworks"
        + getWhere(queryObject)
        + getOrder(pageable.getSort(), FILTER_SORT_PROPERTY_TO_DB_COLUMNS_MAPPING)
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(HomeworkFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    return "SELECT COUNT(*) FROM homeworks" + getWhere(queryObject);
  }

  @Override
  protected String buildWhereClause(HomeworkFilterOptions queryObject) {
    var titleCriteria =
        Optional.ofNullable(queryObject.title()).map(title -> " title ILIKE '%" + title + "%'");
    var lessonIdCriteria =
        Optional.ofNullable(queryObject.lessonId())
            .map(lessonId -> " lesson_id = '" + lessonId + "'");

    var publishDateCriteria =
        getRangeClause(queryObject.publishDateFrom(), queryObject.publishDateTo(), "publish_date");
    var deadlineCriteria =
        getRangeClause(queryObject.deadlineFrom(), queryObject.deadlineTo(), "deadline_date");
    var isGroupCriteria =
        Optional.ofNullable(queryObject.isGroup()).map(isGroup -> " is_group = " + isGroup);

    return Stream.of(
            titleCriteria, lessonIdCriteria, publishDateCriteria, deadlineCriteria, isGroupCriteria)
        .flatMap(Optional::stream)
        .collect(java.util.stream.Collectors.joining(" AND "));
  }
}
