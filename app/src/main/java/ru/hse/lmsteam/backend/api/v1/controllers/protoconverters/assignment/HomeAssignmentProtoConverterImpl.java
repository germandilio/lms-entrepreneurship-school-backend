package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.assignment;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.schema.api.assignments.HomeAssignment;

@Component
public class HomeAssignmentProtoConverterImpl implements HomeAssignmentProtoConverter {
  @Override
  public HomeAssignment map(ru.hse.lmsteam.backend.domain.HomeAssignment homeAssignment)
      throws InvalidProtocolBufferException {
    var builder = HomeAssignment.parseFrom(homeAssignment.payload()).toBuilder();
    builder.setId(homeAssignment.id().toString());
    builder.setLessonId(homeAssignment.lessonId().toString());
    builder.setTitle(homeAssignment.title());
    builder.setPublishDate(Timestamps.fromMillis(homeAssignment.publishDate().toEpochMilli()));
    builder.setDeadlineDate(Timestamps.fromMillis(homeAssignment.deadlineDate().toEpochMilli()));
    builder.setIsGroupAssignment(homeAssignment.isGroup());
    return builder.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.HomeAssignment map(HomeAssignment homeAssignment) {
    return new ru.hse.lmsteam.backend.domain.HomeAssignment(
        java.util.UUID.fromString(homeAssignment.getId()),
        java.util.UUID.fromString(homeAssignment.getLessonId()),
        homeAssignment.getTitle(),
        java.time.Instant.ofEpochMilli(Timestamps.toMillis(homeAssignment.getPublishDate())),
        java.time.Instant.ofEpochMilli(Timestamps.toMillis(homeAssignment.getDeadlineDate())),
        homeAssignment.getIsGroupAssignment(),
        homeAssignment.toByteArray());
  }
}
