package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.grades;

import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions.SubmissionsProtoConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.CompetitionProtoConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.ExamProtoConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.HomeworkProtoConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks.TestProtoConverter;
import ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.user.UserProtoConverter;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.domain.tasks.*;
import ru.hse.lmsteam.schema.api.grades.Grade;
import ru.hse.lmsteam.schema.api.grades.TrackerGrade;

@Component
@RequiredArgsConstructor
public class GradeProtoConverterImpl implements GradeProtoConverter {
  private final UserProtoConverter userProtoConverter;
  private final SubmissionsProtoConverter submissionsProtoConverter;
  private final HomeworkProtoConverter homeworkProtoConverter;
  private final ExamProtoConverter examProtoConverter;
  private final CompetitionProtoConverter competitionProtoConverter;
  private final TestProtoConverter testProtoConverter;

  @Override
  public Grade map(ru.hse.lmsteam.backend.domain.grades.Grade grade, Optional<Lesson> lessonOpt) {
    if (grade.submission() != null && lessonOpt.isEmpty()) {
      throw new IllegalArgumentException("Lesson is required for grades with submission!");
    }
    var gradeBuilder = Grade.newBuilder();
    gradeBuilder.setId(grade.id().toString());
    gradeBuilder.setGradeOwner(userProtoConverter.toSnippet(grade.owner()));
    if (grade.adminGrade() != null) {
      gradeBuilder.setAdminGrade(Int32Value.of(grade.adminGrade()));
    }
    if (grade.adminComment() != null) {
      gradeBuilder.setAdminComment(StringValue.of(grade.adminComment()));
    }

    gradeBuilder.addAllTrackerGrades(
        grade.trackerGradesList().stream().map(this::buildTrackerGrade).toList());

    if (grade.submission() != null) {
      gradeBuilder.setSubmissionForGrading(
          submissionsProtoConverter.convertToProto(grade.submission(), lessonOpt.get()));
    }
    return setTask(gradeBuilder, grade.task(), lessonOpt).build();
  }

  private TrackerGrade buildTrackerGrade(
      ru.hse.lmsteam.backend.domain.grades.TrackerGrade trackerGrade) {
    var builder = TrackerGrade.newBuilder();
    builder.setTracker(userProtoConverter.toSnippet(trackerGrade.tracker()));
    if (trackerGrade.trackerGrade() != null) {
      builder.setGrade(Int32Value.of(trackerGrade.trackerGrade()));
    }
    if (trackerGrade.trackerComment() != null) {
      builder.setComment(StringValue.of(trackerGrade.trackerComment()));
    }
    return builder.build();
  }

  private Grade.Builder setTask(Grade.Builder builder, Task task, Optional<Lesson> lessonOpt) {
    return switch (task) {
      case Homework hw ->
          builder.setHomework(homeworkProtoConverter.toSnippet(hw, lessonOpt.get()));
      case Test test -> builder.setTest(testProtoConverter.toSnippet(test, lessonOpt.get()));
      case Exam exam -> builder.setExam(examProtoConverter.toSnippet(exam));
      case Competition competition ->
          builder.setCompetition(competitionProtoConverter.toSnippet(competition));
      default -> throw new IllegalArgumentException("Unknown type of task.");
    };
  }
}
