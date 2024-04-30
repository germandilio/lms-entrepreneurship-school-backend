package ru.hse.lmsteam.backend.service.exceptions;

public class BusinessLogicBadRequestException extends RuntimeException {
  public BusinessLogicBadRequestException(String message) {
    super(message);
  }

  public BusinessLogicBadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessLogicBadRequestException(Throwable cause) {
    super(cause);
  }
}
