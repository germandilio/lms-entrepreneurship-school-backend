package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.assignment;

import com.google.protobuf.InvalidProtocolBufferException;
import ru.hse.lmsteam.backend.domain.HomeAssignment;

public interface HomeAssignmentProtoConverter {
  ru.hse.lmsteam.schema.api.assignments.HomeAssignment map(HomeAssignment homeAssignment)
      throws InvalidProtocolBufferException;

  HomeAssignment map(ru.hse.lmsteam.schema.api.assignments.HomeAssignment homeAssignment);
}
