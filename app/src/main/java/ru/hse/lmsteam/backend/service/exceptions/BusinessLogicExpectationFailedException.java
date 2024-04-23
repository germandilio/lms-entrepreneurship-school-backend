package ru.hse.lmsteam.backend.service.exceptions;

public class BusinessLogicExpectationFailedException extends RuntimeException {
  public BusinessLogicExpectationFailedException(String message) {
    super(message);
  }

  public BusinessLogicExpectationFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  public BusinessLogicExpectationFailedException(Throwable cause) {
    super(cause);
  }
}
