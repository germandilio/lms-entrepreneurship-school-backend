package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.submissions;

import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.domain.submission.Submission;

public interface SubmissionsProtoConverter {
  ru.hse.lmsteam.schema.api.submissions.Submission convertToProto(
      Submission submission, Lesson lesson);
}
