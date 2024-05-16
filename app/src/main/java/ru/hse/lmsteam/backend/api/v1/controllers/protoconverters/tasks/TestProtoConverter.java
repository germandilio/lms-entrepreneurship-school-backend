package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import ru.hse.lmsteam.backend.domain.lesson.Lesson;
import ru.hse.lmsteam.backend.domain.tasks.Test;
import ru.hse.lmsteam.schema.api.tests.CreateOrUpdateTest;

public interface TestProtoConverter {
  ru.hse.lmsteam.schema.api.tests.Test map(Test test, Lesson lesson);

  Test map(ru.hse.lmsteam.schema.api.tests.Test test);

  ru.hse.lmsteam.schema.api.tests.TestSnippet toSnippet(Test test, Lesson lesson);

  Test retrieveModel(CreateOrUpdateTest.Request test);
}
