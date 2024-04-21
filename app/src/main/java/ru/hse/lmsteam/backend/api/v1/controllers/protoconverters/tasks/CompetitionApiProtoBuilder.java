package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import org.springframework.data.domain.Page;
import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.schema.api.competitions.CreateOrUpdateCompetition;
import ru.hse.lmsteam.schema.api.competitions.DeleteCompetition;
import ru.hse.lmsteam.schema.api.competitions.GetCompetition;
import ru.hse.lmsteam.schema.api.competitions.GetCompetitions;

public interface CompetitionApiProtoBuilder {

  ru.hse.lmsteam.backend.domain.tasks.Competition retrieveCompetitionModel(
      CreateOrUpdateCompetition.Request competition);

  GetCompetition.Response buildGetCompetitionResponse(
      ru.hse.lmsteam.backend.domain.tasks.Competition competition);

  CreateOrUpdateCompetition.Response buildCreateOrUpdateCompetitionResponse(
      ru.hse.lmsteam.backend.domain.tasks.Competition competition);

  DeleteCompetition.Response buildDeleteCompetitionResponse(long itemsDeleted);

  GetCompetitions.Response buildGetCompetitionsResponse(Page<Competition> competitions);
}
