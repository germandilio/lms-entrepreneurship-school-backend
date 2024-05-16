package ru.hse.lmsteam.backend.repository.query.translators;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.grades.GradesFilterOptions;

@Component
public class GradesFilterOptionsQT extends AbstractSimpleQueryTranslator<GradesFilterOptions> {
  private static final ImmutableMap<String, String> sortMappings =
      ImmutableMap.of("grade", "grades.admin_grade");

  @Override
  protected String buildWhereClause(GradesFilterOptions queryObject) {
    var gradeClause =
        getRangeClause(queryObject.gradeFrom(), queryObject.gradeTo(), "grades.admin_grade");

    var taskClause =
        Optional.ofNullable(queryObject.taskId()).map(id -> "grades.task_id = '" + id + "'");
    var trackerClause =
        Optional.ofNullable(queryObject.gradedByTrackerId())
            .map(id -> "tracker_grades.tracker_id = '" + id + "'");
    var gradedByAdminClause =
        Optional.ofNullable(queryObject.gradedByAdmin())
            .map(
                gradedByAdmin -> {
                  if (gradedByAdmin) {
                    return "grades.grade != NULL";
                  } else {
                    return "grades.grade = NULL";
                  }
                });

    var ownersCriteria =
        Optional.ofNullable(queryObject.ownersId())
            .filter(owners -> !owners.isEmpty())
            .map(
                owners ->
                    "grades.owner_id IN ("
                        + owners.stream()
                            .map(id -> "'" + id + "'")
                            .collect(Collectors.joining(", "))
                        + ")");
    var taskTypeClause =
        Optional.ofNullable(queryObject.taskType())
            .map(taskType -> "grades.task_type = '" + taskType + "'");

    return Stream.of(
            taskClause,
            ownersCriteria,
            gradedByAdminClause,
            gradeClause,
            trackerClause,
            taskTypeClause)
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
        + getOrder(pageable.getSort(), sortMappings)
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
