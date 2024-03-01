package ru.hse.lmsteam.backend.model;

public record Sex(String enumSex) {
  public static Sex valueOf(String enumSex) {
    return new Sex(enumSex);
  }
}
