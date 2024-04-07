package ru.hse.lmsteam.backend.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("groups")
public record Group(
    @Id Integer id,
    Integer number, /*Number of the team (user defined) */
    String title,
    String description,
    Boolean isDeleted) {

  /**
   * Merges the current group with the given one. Priority is given to the given group.
   *
   * @param group The group to merge with (low priority fields )
   * @param clearId If true, the id of the current group will be cleared
   * @return
   */
  public Group mergeWith(Group group, boolean clearId) {
    var result = builder();
    if (!clearId) {
      result.id(id == null ? group.id : id);
    }
    result.number(number == null ? group.number : number);
    result.title(title == null ? group.title : title);
    result.description(description == null ? group.description : description);
    result.isDeleted(isDeleted == null ? group.isDeleted : isDeleted);
    return result.build();
  }
}
