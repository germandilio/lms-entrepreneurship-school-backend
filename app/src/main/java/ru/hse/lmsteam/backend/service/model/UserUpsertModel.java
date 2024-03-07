package ru.hse.lmsteam.backend.service.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import ru.hse.lmsteam.backend.domain.user.Sex;
import ru.hse.lmsteam.backend.domain.user.User;

@Builder
public record UserUpsertModel(
    @Nullable UUID id,
    String name,
    String surname,
    @Nullable String patronymic,
    @Nullable String messengerContact,
    Sex sex,
    String email,
    @Nullable String phoneNumber) {

  /**
   * Applies upsert model onto user domain model. It means, that all values provided in upsert model
   * will override that value from user domain model.
   *
   * @param user user domain model
   * @return updated user domain model
   */
  public User mergeWith(@NotNull User user) {
    return mergeWith(user, false);
  }

  /**
   * Applies upsert model onto user domain model. It means, that all values provided in upsert model
   * will override that value from user domain model.
   *
   * @param user user domain model
   * @param eraseEntityId clear id in merged entity
   * @return updated user domain model
   */
  public User mergeWith(@NotNull User user, boolean eraseEntityId) {
    var finalId = eraseEntityId ? null : (id != null ? id : user.id());
    return User.builder()
        .id(finalId)
        .name(name != null ? name : user.name())
        .surname(surname != null ? surname : user.surname())
        .patronymic(patronymic != null ? patronymic : user.patronymic())
        .messengerContact(messengerContact != null ? messengerContact : user.messengerContact())
        .sex(sex != null ? sex : user.sex())
        .email(email != null ? email : user.email())
        .phoneNumber(phoneNumber != null ? phoneNumber : user.phoneNumber())
        .balance(user.balance())
        .isDeleted(user.isDeleted())
        .build();
  }
}
