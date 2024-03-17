package ru.hse.lmsteam.backend.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("users_auth")
public record UserAuth(
    @Id UUID userId,
    String login,
    @With String password,
    UUID token,
    UserRole role,
    Boolean isDeleted) {}
