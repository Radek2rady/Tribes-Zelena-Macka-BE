package com.greenfoxacademy.zelenamackatribes.buildings.controllerAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.greenfoxacademy.zelenamackatribes.buildings.controllers.BuildingController;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.BuildingIdNotBelongToKingdomException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingUpgradeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MaximumLevelReachedException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MissingParamException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import java.util.InputMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {BuildingController.class})
public class BuildingControllerAdvice {

  private static Logger logger = LogManager.getLogger(BuildingControllerAdvice.class);

  @ExceptionHandler(InvalidBuildingTypeException.class)
  public ResponseEntity<?> invalidBuildingType(Exception e) {
    logger.error("HttpStatus: 406 " + e.getMessage());
    return ResponseEntity
        .status(406)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(InvalidBuildingIdException.class)
  public ResponseEntity<?> invalidBuildingId(Exception e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return ResponseEntity
        .status(404)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(MissingParamException.class)
  public ResponseEntity<?> missingParam(Exception e) {
    logger.error("HttpStatus: 400 " + e.getMessage());
    return ResponseEntity
        .status(400)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(NotEnoughResourcesException.class)
  public ResponseEntity<?> notEnoughResources(Exception e) {
    logger.error("HttpStatus: 409 " + e.getMessage());
    return ResponseEntity
        .status(409)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(JsonProcessingException.class)
  public ResponseEntity<?> errorWhileConvertingJson(Exception e) {
    logger.error("HttpStatus: 400 " + e.getMessage());
    return ResponseEntity
        .status(400)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<?> nullPointer(Exception e) {
    logger.error("HttpStatus: 401 " + e.getMessage());
    return ResponseEntity
        .status(401)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(InputMismatchException.class)
  public ResponseEntity<?> incorrectAmount(Exception e) {
    logger.error("HttpStatus: 400 " + e.getMessage());
    return ResponseEntity
        .status(400)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(ForbiddenActionException.class)
  public ResponseEntity<?> forbiddenAction(Exception e) {
    logger.error("HttpStatus: 403 " + e.getMessage());
    return ResponseEntity
        .status(403)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(InvalidBuildingUpgradeException.class)
  public ResponseEntity<?> buildingsOrTownhallNotReady(Exception e) {
    logger.error("HttpStatus: 406 " + e.getMessage());
    return ResponseEntity
        .status(406)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(MaximumLevelReachedException.class)
  public ResponseEntity<?> maximumLevelReached(Exception e) {
    logger.error("HttpStatus: 406 " + e.getMessage());
    return ResponseEntity
        .status(406)
        .body(new ErrorResponseDTO(e.getMessage()));
  }

  @ExceptionHandler(BuildingIdNotBelongToKingdomException.class)
  public ResponseEntity<?> buildingIdNotBelongToTheKingdom(Exception e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return ResponseEntity
        .status(404)
        .body(new ErrorResponseDTO(e.getMessage()));
  }
}
