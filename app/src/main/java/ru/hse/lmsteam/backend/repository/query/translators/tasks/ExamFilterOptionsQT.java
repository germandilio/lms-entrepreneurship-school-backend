package ru.hse.lmsteam.backend.repository.query.translators.tasks;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.repository.query.translators.AbstractSimpleQueryTranslator;
import ru.hse.lmsteam.backend.service.model.tasks.ExamFilterOptions;

@Component
public class ExamFilterOptionsQT extends AbstractSimpleQueryTranslator<ExamFilterOptions> {

  @Override
  public String translateToSql(ExamFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    return "SELECT * FROM exams"
        + getWhere(queryObject)
        + getOrder(pageable.getSort())
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(ExamFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    return "SELECT COUNT(*) FROM exams" + getWhere(queryObject);
  }

  @Override
  protected String getWhere(ExamFilterOptions queryObject) {
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
        .reduce((a, b) -> a + " AND " + b)
        .map(s -> " WHERE " + s)
        .orElse("");
  }
}
