package ru.hse.lmsteam.backend.domain.user;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

@Builder
@Table("users")
public record User(
    @Id UUID id,
    String name,
    String surname,
    @Nullable String patronymic,
    @Nullable String messengerContact,
    Sex sex,
    String email,
    @Nullable String phoneNumber,
    @Nullable Integer groupId,
    String role,
    BigDecimal balance,
    Boolean isDeleted) {

  public Group getGroup() {
    return null;
  }

  public Mono<Group> lazyGetGroup() {
    return Mono.empty();
  }
}
