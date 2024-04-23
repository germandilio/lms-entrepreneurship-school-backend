package ru.hse.lmsteam.backend.service.exceptions;

public class BusinessLogicNotFoundException extends RuntimeException {
  public BusinessLogicNotFoundException(String message) {
    super(message);
  }

  public BusinessLogicNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessLogicNotFoundException(Throwable cause) {
    super(cause);
  }
}
