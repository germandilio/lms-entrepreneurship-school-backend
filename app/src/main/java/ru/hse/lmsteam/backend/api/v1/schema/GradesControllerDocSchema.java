package ru.hse.lmsteam.backend.api.v1.schema;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.schema.api.grades.GetGrade;
import ru.hse.lmsteam.schema.api.grades.GetGrades;
import ru.hse.lmsteam.schema.api.grades.UpdateGrade;

public interface GradesControllerDocSchema {
  Mono<GetGrade.Response> getGradeById(String token, UUID id);

  Mono<UpdateGrade.Response> updateGrade(String token, UUID id, UpdateGrade.Request request);

  Mono<GetGrades.Response> getGrades(
      String token,
      Integer gradeFrom,
      Integer gradeTo,
      UUID taskId,
      UUID ownerId,
      UUID trackerId,
      Pageable pageable);
}
