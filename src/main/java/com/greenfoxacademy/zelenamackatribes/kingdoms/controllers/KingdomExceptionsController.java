package com.greenfoxacademy.zelenamackatribes.kingdoms.controllers;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.IncorrectPageParameterException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomHasNoTownhallException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomOutOfRangeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KingdomExceptionsController {

  private static Logger logger = LogManager.getLogger(KingdomExceptionsController.class);

  @ExceptionHandler(KingdomNotFoundException.class)
  public ResponseEntity<?> kingdomNotFoundHandler(Exception e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return ResponseEntity
        .status(404)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(ForbiddenActionException.class)
  public ResponseEntity<?> kingdomForbiddenAction(Exception e) {
    logger.error("HttpStatus: 403 " + e.getMessage());
    return ResponseEntity
        .status(403)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(KingdomHasNoTownhallException.class)
  public ResponseEntity<?> kingdomHasNoTownhall(Exception e) {
    logger.error("HttpStatus: 406 " + e.getMessage());
    return ResponseEntity
        .status(406)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(KingdomOutOfRangeException.class)
  public ResponseEntity<?> kingdomOutOfRange(Exception e) {
    logger.error("HttpStatus: 403 " + e.getMessage());
    return ResponseEntity
        .status(403)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(IncorrectPageParameterException.class)
  public ResponseEntity<?> notEnoughKingdomsToDisplayPage(Exception e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return ResponseEntity
        .status(404)
        .body(new ErrorResponseDTO(e.getMessage()));
  }
}
