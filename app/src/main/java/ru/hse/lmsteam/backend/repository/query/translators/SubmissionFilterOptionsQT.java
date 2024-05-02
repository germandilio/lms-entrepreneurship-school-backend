package ru.hse.lmsteam.backend.repository.query.translators;

import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.service.model.submissions.SubmissionFilterOptions;

@Component
public class SubmissionFilterOptionsQT
    extends AbstractSimpleQueryTranslator<SubmissionFilterOptions> {

  @Override
  public String translateToSql(SubmissionFilterOptions queryObject, Pageable pageable) {
    if (queryObject == null) {
      throw new IllegalArgumentException("FilterOptions are null!");
    }
    if (pageable == null) {
      throw new IllegalArgumentException("Pageable is null!");
    }

    return "SELECT * FROM submissions" + getWhere(queryObject) + getLimitAndOffset(pageable);
  }

  @Override
  public String translateToCountSql(SubmissionFilterOptions queryObject) {
    if (queryObject == null) {
      throw new IllegalArgumentException("FilterOptions are null!");
    }

    return "SELECT COUNT(*) FROM submissions" + getWhere(queryObject);
  }

  @Override
  protected String getWhere(SubmissionFilterOptions queryObject) {
    var ownerCriteria =
        Optional.ofNullable(queryObject.ownerId()).map(ownerId -> "owner_id = '" + ownerId + "'");

    var taskCriteria =
        Optional.ofNullable(queryObject.taskId()).map(taskId -> "task_id = '" + taskId + "'");

    var teamCriteria =
        Optional.ofNullable(queryObject.teamId()).map(teamId -> "team_id = '" + teamId + "'");

    var criterias =
        Stream.of(ownerCriteria, taskCriteria, teamCriteria).flatMap(Optional::stream).toList();
    return " WHERE " + String.join(" AND ", criterias);
  }
}
