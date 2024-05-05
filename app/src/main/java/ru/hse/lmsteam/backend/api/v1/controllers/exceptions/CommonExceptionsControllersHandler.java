package ru.hse.lmsteam.backend.api.v1.controllers.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.hse.lmsteam.backend.service.exceptions.*;

@RestControllerAdvice
@Slf4j
public class CommonExceptionsControllersHandler {
  @ExceptionHandler(BusinessLogicNotFoundException.class)
  public ResponseEntity<?> handleNotFoundException(BusinessLogicNotFoundException e) {
    log.info("Not found exception");
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(BusinessLogicUnauthorizedException.class)
  public ResponseEntity<?> handleUnauthorizedException(BusinessLogicUnauthorizedException e) {
    log.info("Unauthorized exception");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
  }

  @ExceptionHandler(BusinessLogicAccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(BusinessLogicAccessDeniedException e) {
    log.info("Access denied exception");
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
  }

  @ExceptionHandler(BusinessLogicExpectationFailedException.class)
  public ResponseEntity<?> handleExpectationFailedException(
      BusinessLogicExpectationFailedException e) {
    log.info("Expectations failed exception");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
  }

  @ExceptionHandler(BusinessLogicConflictException.class)
  public ResponseEntity<?> handleConflictException(BusinessLogicConflictException e) {
    log.info("Conflict exception");
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
  }
}
