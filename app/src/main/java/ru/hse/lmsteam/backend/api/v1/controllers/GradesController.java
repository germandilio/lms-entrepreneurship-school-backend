package ru.hse.lmsteam.backend.api.v1.controllers;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.grades.GradesApiProtoBuilder;
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
  private final GradesApiProtoBuilder gradesApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetGrade.Response> getGradeById(
      @RequestHeader("Authorization") String rawToken, @PathVariable UUID id) {
    return null;
  }

  @PatchMapping("/{id}")
  @Override
  public Mono<UpdateGrade.Response> updateGrade(
      @RequestHeader("Authorization") String rawToken,
      @PathVariable UUID id,
      @RequestBody UpdateGrade.Request request) {
    return null;
  }

  @GetMapping("/list")
  @Override
  public Mono<GetGrades.Response> getGrades(
      @RequestHeader("Authorization") String rawToken,
      @RequestParam(required = false) Integer gradeFrom,
      @RequestParam(required = false) Integer gradeTo,
      @RequestParam(required = false) UUID taskId,
      @RequestParam(required = false) UUID ownerId,
      Pageable pageable) {
    return null;
  }
}
