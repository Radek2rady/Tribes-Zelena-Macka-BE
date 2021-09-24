package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.controllerAdvice;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ValuesFileControllerAdvice {

  private static Logger logger = LogManager.getLogger(ValuesFileControllerAdvice.class);

  @ExceptionHandler(DefaultValuesFileException.class)
  public ResponseEntity<?> invalidToken(Exception e) {
    logger.error("HttpStatus: 500 " + e.getMessage());
    return ResponseEntity
        .status(500)
        .body(new ErrorResponseDTO(e.getMessage()));
  }
}
