package ru.hse.lmsteam.backend.domain.user_teams;

import java.util.UUID;
import lombok.Builder;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("users_auth")
public record UserAuth(
    @Id Integer id,
    UUID userId,
    String login,
    @With String password,
    UUID passwordResetToken,
    UserRole role,
    Boolean isDeleted) {}
