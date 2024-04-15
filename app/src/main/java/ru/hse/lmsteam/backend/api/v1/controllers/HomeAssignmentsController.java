package ru.hse.lmsteam.backend.api.v1.controllers;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.assignment.HomeAssignmentApiProtoBuilder;
import ru.hse.lmsteam.backend.api.v1.schema.HomeAssignmentsControllerSchemaDoc;
import ru.hse.lmsteam.backend.service.assignments.HomeAssignmentManager;
import ru.hse.lmsteam.schema.api.assignments.*;

@RestController
@RequestMapping(
    value = "/api/v1/home-assignments",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PROTOBUF_VALUE})
@RequiredArgsConstructor
public class HomeAssignmentsController implements HomeAssignmentsControllerSchemaDoc {
  private final HomeAssignmentManager homeAssignmentManager;
  private final HomeAssignmentApiProtoBuilder homeAssignmentApiProtoBuilder;

  @GetMapping("/{id}")
  @Override
  public Mono<GetHomeAssigment.Response> getHomeAssignment(@PathVariable UUID id) {
    return null;
  }

  @Override
  public Mono<CreateHomeAssignment.Response> createHomeAssignment(
      CreateHomeAssignment.Request request) {
    return null;
  }

  @Override
  public Mono<UpdateHomeAssignment.Response> updateHomeAssignment(
      UpdateHomeAssignment.Request request) {
    return null;
  }

  @Override
  public Mono<DeleteHomeAssignment.Response> deleteHomeAssignment(UUID id) {
    return null;
  }

  @Override
  public Mono<GetHomeAssignments.Response> getHomeAssignments(
      UUID lessonId,
      String title,
      Instant deadlineFrom,
      Instant deadlineTo,
      Instant publishFrom,
      Instant publishTo,
      Pageable pageable) {
    return null;
  }
}
