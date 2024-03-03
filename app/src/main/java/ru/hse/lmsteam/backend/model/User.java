package ru.hse.lmsteam.backend.model;

import java.math.BigDecimal;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("users")
public record User(
    @Id String id,
    String name,
    String surname,
    String patronymicOpt,
    String messengerContact,
    Sex sex,
    String email,
    String phoneNumber,
    BigDecimal balance,
    Float finalGradeBonus) {}
