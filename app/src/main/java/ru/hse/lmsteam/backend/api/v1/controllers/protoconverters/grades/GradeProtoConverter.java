package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.grades;

import java.util.Optional;
import ru.hse.lmsteam.backend.domain.grades.Grade;
import ru.hse.lmsteam.backend.domain.lesson.Lesson;

public interface GradeProtoConverter {
  ru.hse.lmsteam.schema.api.grades.Grade map(Grade grade, Optional<Lesson> lessonOpt);
}
