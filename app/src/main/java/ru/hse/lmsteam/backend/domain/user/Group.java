package ru.hse.lmsteam.backend.domain.user;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Flux;

@Builder
@Table("groups")
public record Group(
    @Id Integer id,
    Integer number, /*Number of the team (user defined) */
    String title,
    String description,
    Boolean isDeleted) {
  public ImmutableList<User> getMembers() {
    return null;
  }

  public Flux<User> lazyGetMembers() {
    return null;
  }
}
