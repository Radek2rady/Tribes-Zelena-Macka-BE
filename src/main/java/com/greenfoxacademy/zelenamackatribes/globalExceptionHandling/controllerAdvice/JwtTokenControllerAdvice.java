package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.controllerAdvice;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JwtTokenControllerAdvice {

  private static Logger logger = LogManager.getLogger(JwtTokenControllerAdvice.class);

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<?> invalidToken(Exception e) {
    logger.error("HttpStatus: 401 " + e.getMessage());
    return ResponseEntity
        .status(401)
        .body(new ErrorResponseDTO(e.getMessage()));
  }
}
