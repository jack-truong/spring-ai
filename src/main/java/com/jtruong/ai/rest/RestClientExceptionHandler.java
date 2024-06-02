package com.jtruong.ai.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class RestClientExceptionHandler {

  @ExceptionHandler(RestClientException.class)
  public ResponseEntity<String> restClientException(RestClientException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred trying to communicate with the AI endpoint.");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> illegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("An illegal argument was passed to the request: %s", ex.getMessage()));
  }
}
