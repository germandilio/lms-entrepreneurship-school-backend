package ru.hse.lmsteam.backend.service.model.user;

import java.util.Optional;
import java.util.UUID;

public record UserSnippet(UUID userId, String name, String surname, Optional<String> patronymic) {}
