package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.assignment;

import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.HomeAssignment;
import ru.hse.lmsteam.schema.api.assignments.*;

public interface HomeAssignmentApiProtoBuilder {
  GetHomeAssigment.Response buildGetHomeAssignmentResponse(HomeAssignment group);

  CreateHomeAssignment.Response buildCreateHomeAssignmentResponse(HomeAssignment group);

  UpdateHomeAssignment.Response buildUpdateHomeAssignmentResponse(HomeAssignment group);

  DeleteHomeAssignment.Response buildDeleteHomeAssignmentResponse(long itemsDeleted);

  GetHomeAssignments.Response buildGetHomeAssignmentsResponse(Page<HomeAssignment> assignments);

  HomeAssignment retrieveHomeAssignmentModel(UpdateHomeAssignment.Request request);
}
