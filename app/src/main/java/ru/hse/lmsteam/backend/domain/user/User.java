package ru.hse.lmsteam.backend.domain.user;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("users")
public record User(
    @Id UUID id,
    String name,
    String surname,
    String patronymic,
    String messengerContact,
    Sex sex,
    String email,
    String phoneNumber,
    BigDecimal balance,
    Boolean isDeleted) {}
