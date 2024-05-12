package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.grades;

import org.springframework.data.domain.Page;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.domain.grades.Grade;
import ru.hse.lmsteam.schema.api.grades.GetGrade;
import ru.hse.lmsteam.schema.api.grades.GetGrades;
import ru.hse.lmsteam.schema.api.grades.UpdateGrade;

public interface GradesApiProtoBuilder {
  Mono<GetGrade.Response> buildGetGradeResponse(Grade grade);

  Mono<GetGrades.Response> buildGetGradeResponse(Page<Grade> grades);

  Mono<UpdateGrade.Response> buildUpdateGradeResponse(Grade grade);
}
