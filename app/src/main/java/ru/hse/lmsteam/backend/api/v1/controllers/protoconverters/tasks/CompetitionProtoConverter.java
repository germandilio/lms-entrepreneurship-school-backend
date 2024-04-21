package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.schema.api.competitions.CreateOrUpdateCompetition;

public interface CompetitionProtoConverter {
  ru.hse.lmsteam.schema.api.competitions.Competition map(Competition task);

  Competition map(ru.hse.lmsteam.schema.api.competitions.Competition task);

  Competition retrieveModel(CreateOrUpdateCompetition.Request request);
}
