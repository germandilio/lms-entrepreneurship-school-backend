package ru.hse.lmsteam.backend.repository.query.translators;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.assignments.HomeAssignmentFilterOptions;

@Component
public class HomeAssignmentFilterOptionsQT
    extends AbstractSimpleQueryTranslator<HomeAssignmentFilterOptions> {

  @Override
  public String translateToSql(HomeAssignmentFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Cannot translate queryObject to sql with null pageable!");
    }

    return "SELECT * FROM home_assignments"
        + getWhere(queryObject)
        + getOrder(pageable.getSort())
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(HomeAssignmentFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("Cannot translate null queryObject to sql!");
    }
    return "SELECT COUNT(*) FROM home_assignments" + getWhere(queryObject);
  }

  @Override
  protected String getWhere(HomeAssignmentFilterOptions queryObject) {
    var titleCriteria =
        Optional.ofNullable(queryObject.title())
            .map(title -> " projectTheme ILIKE '%" + title + "%'");
    var lessonIdCriteria =
        Optional.ofNullable(queryObject.lessonId())
            .map(lessonId -> " lesson_id = '" + lessonId + "'");

    var publishDateCriteria =
        getTimestampRangeClause(queryObject.publishDateFrom(), queryObject.publishDateTo())
            .map(s -> " publish_date" + s);

    var deadlineCriteria =
        getTimestampRangeClause(queryObject.deadlineFrom(), queryObject.deadlineTo())
            .map(s -> " deadline_date" + s);

    return Stream.of(titleCriteria, lessonIdCriteria, publishDateCriteria, deadlineCriteria)
        .flatMap(Optional::stream)
        .reduce((a, b) -> a + " AND " + b)
        .map(s -> " WHERE " + s)
        .orElse("");
  }

  private Optional<String> getTimestampRangeClause(Instant from, Instant to) {
    if (from != null && to != null) {
      return Optional.of(" IS NOT NULL AND BETWEEN '" + from + "' AND '" + to + "'");
    } else if (from != null) {
      return Optional.of(" IS NOT NULL AND >= '" + from + "'");
    } else if (to != null) {
      return Optional.of(" IS NOT NULL AND <= '" + to + "'");
    } else {
      return Optional.empty();
    }
  }
}
