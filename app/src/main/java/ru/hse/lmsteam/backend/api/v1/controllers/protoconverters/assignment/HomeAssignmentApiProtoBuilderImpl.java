package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.assignment;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.HomeAssignment;
import ru.hse.lmsteam.schema.api.assignments.*;

@Component
public class HomeAssignmentApiProtoBuilderImpl implements HomeAssignmentApiProtoBuilder {
  @Override
  public GetHomeAssigment.Response buildGetHomeAssignmentResponse(HomeAssignment group) {
    return null;
  }

  @Override
  public CreateHomeAssignment.Response buildCreateHomeAssignmentResponse(HomeAssignment group) {
    return null;
  }

  @Override
  public UpdateHomeAssignment.Response buildUpdateHomeAssignmentResponse(HomeAssignment group) {
    return null;
  }

  @Override
  public DeleteHomeAssignment.Response buildDeleteHomeAssignmentResponse(long itemsDeleted) {
    return null;
  }

  @Override
  public GetHomeAssignments.Response buildGetHomeAssignmentsResponse(
      Page<HomeAssignment> assignments) {
    return null;
  }

  @Override
  public HomeAssignment retrieveHomeAssignmentModel(UpdateHomeAssignment.Request request) {
    return null;
  }
}
