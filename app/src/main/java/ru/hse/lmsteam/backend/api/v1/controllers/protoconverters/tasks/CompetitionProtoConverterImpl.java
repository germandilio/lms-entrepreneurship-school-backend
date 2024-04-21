package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.Timestamps;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.schema.api.competitions.Competition;
import ru.hse.lmsteam.schema.api.competitions.CreateOrUpdateCompetition;

@Component
public class CompetitionProtoConverterImpl implements CompetitionProtoConverter {
  @Override
  public Competition map(ru.hse.lmsteam.backend.domain.tasks.Competition task) {
    try {
      var builder = Competition.parseFrom(task.payload()).toBuilder();
      builder.setId(task.id().toString());
      builder.setTitle(task.title());
      if (task.publishDate() != null) {
        builder.setPublishDate(Timestamps.fromMillis(task.publishDate().toEpochMilli()));
      }
      if (task.deadlineDate() != null) {
        builder.setDeadlineDate(Timestamps.fromMillis(task.deadlineDate().toEpochMilli()));
      }
      return builder.build();
    } catch (InvalidProtocolBufferException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Competition map(Competition task) {
    var builder = ru.hse.lmsteam.backend.domain.tasks.Competition.builder();
    if (!task.getId().isBlank()) {
      builder.id(UUID.fromString(task.getId()));
    }
    builder.title(task.getTitle());
    if (task.hasPublishDate()) {
      builder.publishDate(
          java.time.Instant.ofEpochMilli(Timestamps.toMillis(task.getPublishDate())));
    }
    if (task.hasDeadlineDate()) {
      builder.deadlineDate(
          java.time.Instant.ofEpochMilli(Timestamps.toMillis(task.getDeadlineDate())));
    }
    builder.payload(task.toByteArray());
    return builder.build();
  }

  @Override
  public ru.hse.lmsteam.backend.domain.tasks.Competition retrieveModel(
      CreateOrUpdateCompetition.Request request) {
    var builder = Competition.newBuilder();
    builder.setTitle(request.getTitle());
    builder.setDescription(request.getDescription());
    builder.setGradingCriteria(request.getGradingCriteria());
    builder.addAllExternalMaterialUrls(request.getExternalMaterialUrlsList());
    if (request.hasPublishDate()) {
      builder.setPublishDate(request.getPublishDate());
    }
    if (request.hasDeadlineDate()) {
      builder.setDeadlineDate(request.getDeadlineDate());
    }
    return map(builder.build());
  }
}
