package ru.hse.lmsteam.backend.repository.query.translators.tasks;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.repository.query.translators.AbstractSimpleQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.HomeworkFilterOptions;

@Component
public class HomeworkFilterOptionsQT extends AbstractSimpleQueryTranslator<HomeworkFilterOptions> {

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
        + getOrder(pageable.getSort())
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
  protected String getWhere(HomeworkFilterOptions queryObject) {
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
    var isGroupCriteria =
        Optional.ofNullable(queryObject.isGroup()).map(isGroup -> " is_group = " + isGroup);

    return Stream.of(
            titleCriteria, lessonIdCriteria, publishDateCriteria, deadlineCriteria, isGroupCriteria)
        .flatMap(Optional::stream)
        .reduce((a, b) -> a + " AND " + b)
        .map(s -> " WHERE " + s)
        .orElse("");
  }
}
