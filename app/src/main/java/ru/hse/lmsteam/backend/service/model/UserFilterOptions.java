package ru.hse.lmsteam.backend.service.model;

import com.google.common.collect.ImmutableSet;
import lombok.Builder;

@Builder
public record UserFilterOptions(
    String namePattern,
    String emailPattern,
    ImmutableSet<Integer> groupNumbers,
    ImmutableSet<String> roles,
    Boolean isDeleted) {}
