package ru.hse.lmsteam.backend.service.exceptions;

public class BusinessLogicUnauthorizedException extends RuntimeException {
  public BusinessLogicUnauthorizedException(String message) {
    super(message);
  }

  public BusinessLogicUnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessLogicUnauthorizedException(Throwable cause) {
    super(cause);
  }
}
