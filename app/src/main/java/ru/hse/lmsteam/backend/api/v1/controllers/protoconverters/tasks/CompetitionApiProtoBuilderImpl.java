package ru.hse.lmsteam.backend.api.v1.controllers.protoconverters.tasks;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.hse.lmsteam.backend.domain.tasks.Competition;
import ru.hse.lmsteam.schema.api.competitions.*;

@Component
@RequiredArgsConstructor
public class CompetitionApiProtoBuilderImpl implements CompetitionApiProtoBuilder {
  private final CompetitionProtoConverter competitionProtoConverter;

  @Override
  public Competition retrieveCompetitionModel(CreateOrUpdateCompetition.Request competition) {
    return competitionProtoConverter.retrieveModel(competition);
  }

  @Override
  public GetCompetition.Response buildGetCompetitionResponse(Competition competition) {
    var b = GetCompetition.Response.newBuilder();
    if (competition != null) {
      b.setCompetition(competitionProtoConverter.map(competition));
    }
    return b.build();
  }

  @Override
  public CreateOrUpdateCompetition.Response buildCreateOrUpdateCompetitionResponse(
      Competition competition) {
    var b = CreateOrUpdateCompetition.Response.newBuilder();
    if (competition != null) {
      b.setCompetition(competitionProtoConverter.map(competition));
    }
    return b.build();
  }

  @Override
  public DeleteCompetition.Response buildDeleteCompetitionResponse(long itemsDeleted) {
    return DeleteCompetition.Response.newBuilder().setEntriesDeleted(itemsDeleted).build();
  }

  @Override
  public GetCompetitions.Response buildGetCompetitionsResponse(Page<Competition> competitions) {
    var b = GetCompetitions.Response.newBuilder();
    b.setPage(
        ru.hse.lmsteam.schema.api.common.Page.newBuilder()
            .setTotalPages(competitions.getTotalPages())
            .setTotalElements(competitions.getTotalElements())
            .build());
    b.addAllCompetitions(competitions.map(competitionProtoConverter::toSnippet));
    return b.build();
  }
}
