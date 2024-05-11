package ru.hse.lmsteam.backend.api.v1.controllers;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.schema.GradesControllerDocSchema;
import ru.hse.lmsteam.schema.api.grades.GetGrade;
import ru.hse.lmsteam.schema.api.grades.GetGrades;
import ru.hse.lmsteam.schema.api.grades.UpdateGrade;

@RestController
@RequestMapping(
    value = "/api/v1/grades",
    produces = {MediaType.APPLICATION_PROTOBUF_VALUE, MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class GradesController implements GradesControllerDocSchema {
  private final GrantAccessUtils grantAccessUtils;

  @Override
  public Mono<GetGrade.Response> getGradeById(
      @RequestHeader("Authorization") String rawToken, UUID id) {
    return null;
  }

  @Override
  public Mono<UpdateGrade.Response> updateGrade(
      @RequestHeader("Authorization") String rawToken, UUID id, UpdateGrade.Request request) {
    return null;
  }

  @Override
  public Mono<GetGrades.Response> getGrades(
      @RequestHeader("Authorization") String rawToken,
      Integer gradeFrom,
      Integer gradeTo,
      UUID taskId,
      UUID ownerId,
      UUID trackerId,
      Pageable pageable) {
    return null;
  }
}
