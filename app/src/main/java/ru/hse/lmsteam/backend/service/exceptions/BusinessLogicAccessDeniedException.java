package ru.hse.lmsteam.backend.service.exceptions;

public class BusinessLogicAccessDeniedException extends RuntimeException {
  public BusinessLogicAccessDeniedException(String message) {
    super(message);
  }

  public BusinessLogicAccessDeniedException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessLogicAccessDeniedException(Throwable cause) {
    super(cause);
  }
}
