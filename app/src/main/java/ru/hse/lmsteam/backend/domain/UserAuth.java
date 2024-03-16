package ru.hse.lmsteam.backend.domain;

import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("users_auth")
public record UserAuth(
    @Id UUID userId, String login, String password, String role, Boolean isDeleted) {}
