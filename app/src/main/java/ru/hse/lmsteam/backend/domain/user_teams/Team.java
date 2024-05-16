package ru.hse.lmsteam.backend.domain.user_teams;

import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("teams")
public record Team(
    @Id UUID id,
    Integer number, /*Number of the team (user defined) */
    String projectTheme,
    String description,
    Boolean isDeleted) {

  /**
   * Merges the current group with the given one. Priority is given to the given group.
   *
   * @param team The group to merge with (low priority fields )
   * @param clearId If true, the id of the current group will be cleared
   * @return
   */
  public Team mergeWith(Team team, boolean clearId) {
    var result = builder();
    if (!clearId) {
      result.id(id == null ? team.id : id);
    }
    result.number(number == null ? team.number : number);
    result.projectTheme(projectTheme == null ? team.projectTheme : projectTheme);
    result.description(description == null ? team.description : description);
    result.isDeleted(isDeleted == null ? team.isDeleted : isDeleted);
    return result.build();
  }
}
