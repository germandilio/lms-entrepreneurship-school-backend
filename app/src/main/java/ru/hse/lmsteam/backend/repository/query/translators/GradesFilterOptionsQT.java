package ru.hse.lmsteam.backend.repository.query.translators;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

@Component
public class GradesFilterOptionsQT extends AbstractSimpleQueryTranslator<GradesFilterOptions> {

  @Override
  protected String buildWhereClause(GradesFilterOptions queryObject) {
    var gradeClause =
        getRangeClause(queryObject.gradeFrom(), queryObject.gradeTo(), "grades.admin_grade");

    var taskClause =
        Optional.ofNullable(queryObject.taskId()).map(id -> "grades.task_id = '" + id + "'");
    var ownerClause =
        Optional.ofNullable(queryObject.ownerId()).map(id -> "grades.owner_id = '" + id + "'");
    var trackerClause =
        Optional.ofNullable(queryObject.trackerId())
            .map(id -> "tracker_grades.tracker_id = '" + id + "'");

    return Stream.of(gradeClause, taskClause, ownerClause, trackerClause)
        .flatMap(Optional::stream)
        .collect(Collectors.joining(" AND "));
  }

  @Override
  public String translateToSql(GradesFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("queryObject is null");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("pageable is null");
    }

    return "SELECT grades.* FROM grades LEFT JOIN trackers_grades ON grades.id = tracker_grades.grade_id"
        + getWhere(queryObject)
        + " GROUP BY grades.id"
        + getOrder(pageable.getSort(), null)
        + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(GradesFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("queryObject is null");
    }

    return "SELECT COUNT(grades.*) FROM grades LEFT JOIN trackers_grades ON grades.id = tracker_grades.grade_id"
        + getWhere(queryObject);
  }
}
