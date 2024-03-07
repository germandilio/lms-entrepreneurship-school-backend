package ru.hse.lmsteam.backend.domain.user;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("groups")
public record Group(@Id Integer id, Integer number, String title, String description) {}
