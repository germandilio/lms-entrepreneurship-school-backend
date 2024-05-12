package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions;

import com.google.protobuf.util.Timestamps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.HomeworkProtoConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.teams.TeamSnippetConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UserProtoConverter;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.schema.api.submissions.Submission;

@Component
@RequiredArgsConstructor
public class SubmissionsProtoConverterImpl implements SubmissionsProtoConverter {
  private final HomeworkProtoConverter homeworkProtoConverter;
  private final UserProtoConverter userProtoConverter;
  private final TeamSnippetConverter teamSnippetConverter;

  @Override
  public Submission convertToProto(
      ru.hse.lmsteam.backend.domain.submission.Submission submission, Lesson lesson) {
    var builder = Submission.newBuilder();
    builder.setId(submission.id().toString());
    builder.setHomework(homeworkProtoConverter.toSnippet(submission.homework(), lesson));
    builder.setOwner(userProtoConverter.toSnippet(submission.owner()));
    builder.setPublisher(userProtoConverter.toSnippet(submission.publisher()));
    if (submission.team() != null) {
      builder.setTeam(teamSnippetConverter.toSnippet(submission.team()));
    }
    builder.setPublishedAt(Timestamps.fromMillis(submission.submissionDate().toEpochMilli()));
    builder.setPayload(submission.payload());
    return builder.build();
  }
}
