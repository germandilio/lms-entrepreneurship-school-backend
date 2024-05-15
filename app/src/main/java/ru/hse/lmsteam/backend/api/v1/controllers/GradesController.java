package ru.hse.lmsteam.backend.api.v1.controllers;

import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.grades.GradesApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.GradesControllerDocSchema;
import ru.hse.lmsteam.backend.domain.user_teams.UserRole;
import ru.hse.lmsteam.backend.service.exceptions.BusinessLogicNotFoundException;
import ru.hse.lmsteam.backend.service.grades.GradesManager;
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
  private final GradesManager gradesManager;

  @GetMapping("/{id}")
  @Override
  public Mono<GetGrade.Response> getGradeById(
      @RequestHeader("Authorization") String rawToken, @PathVariable UUID id) {
    return grantAccessUtils
        .grantAccess(rawToken, GrantAccessUtils.ALL_INTERNALS)
        .then(
            gradesManager
                .getGrade(id)
                .switchIfEmpty(Mono.error(new BusinessLogicNotFoundException("Grade not found.")))
                .flatMap(gradesApiProtoBuilder::buildGetGradeResponse));
  }

  @PatchMapping("/{id}")
  @Override
  public Mono<UpdateGrade.Response> updateGrade(
      @RequestHeader("Authorization") String rawToken,
      @PathVariable UUID id,
      @RequestBody UpdateGrade.Request request) {
    return grantAccessUtils
        .grantAccess(rawToken, Set.of(UserRole.ADMIN, UserRole.TRACKER))
        .flatMap(
            userAuth -> {
              var comment = request.hasComment() ? request.getComment().getValue() : null;
              return gradesManager
                  .updateGrade(id, userAuth, request.getGrade(), comment)
                  .flatMap(gradesApiProtoBuilder::buildUpdateGradeResponse);
            });
  }

  @GetMapping("/list")
  @Override
  public Mono<GetGrades.Response> getGrades(
      @RequestHeader("Authorization") String rawToken,
      @RequestParam(required = false) Integer gradeFrom,
      @RequestParam(required = false) Integer gradeTo,
      @RequestParam(required = false) UUID taskId,
      @RequestParam(required = false) UUID learnerId,
      @RequestParam(required = false) Boolean isGradedByPerformer,
      Pageable pageable) {
    return grantAccessUtils
        .grantAccess(rawToken, GrantAccessUtils.ALL_INTERNALS)
        .flatMap(
            performer ->
                gradesManager.buildGradesFilterOptions(
                    performer, taskId, learnerId, isGradedByPerformer, gradeFrom, gradeTo))
        .flatMap(
            filterOptions ->
                gradesManager
                    .getAllGrades(filterOptions, pageable)
                    .flatMap(gradesApiProtoBuilder::buildGetGradesResponse));
  }
}
