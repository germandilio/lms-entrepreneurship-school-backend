package ru.hse.lmsteam.backend.service.exceptions;

public class BusinessLogicConflictException extends RuntimeException {
  public BusinessLogicConflictException(String message) {
    super(message);
  }

  public BusinessLogicConflictException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessLogicConflictException(Throwable cause) {
    super(cause);
  }
}
