package com.greenfoxacademy.zelenamackatribes.troops.controllerAdvice;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidAcademyException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidOwnerException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.troops.controllers.TroopController;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.InvalidTroopUpgradeException;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.TroopNotFoundException;
import java.util.InputMismatchException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {TroopController.class})
public class TroopControllerAdvice {

  private static Logger logger = LogManager.getLogger(TroopControllerAdvice.class);

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(InvalidBuildingIdException.class)
  public ErrorResponseDTO invalidBuildingId(InvalidBuildingIdException e) {
    logger.error("HttpStatus: " + HttpStatus.NOT_ACCEPTABLE + " InvalidBuildingIdException " + e
        .getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(InvalidOwnerException.class)
  public ErrorResponseDTO incorrectInput(InvalidOwnerException e) {
    logger
        .error("HttpStatus: " + HttpStatus.FORBIDDEN + " InvalidOwnerException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(InvalidAcademyException.class)
  public ErrorResponseDTO invalidAcademyId(InvalidAcademyException e) {
    logger.error(
        "HttpStatus: " + HttpStatus.NOT_ACCEPTABLE + " InvalidAcademyException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(NotEnoughResourcesException.class)
  public ErrorResponseDTO noResources(NotEnoughResourcesException e) {
    logger.error(
        "HttpStatus: " + HttpStatus.CONFLICT + " NotEnoughResourcesException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(TroopNotFoundException.class)
  public ErrorResponseDTO troopNotFound(TroopNotFoundException e) {
    logger.error(
        "HttpStatus: " + HttpStatus.NOT_FOUND + " TroopNotFoundException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(ForbiddenActionException.class)
  public ErrorResponseDTO forbiddenAction(ForbiddenActionException e) {
    logger.error(
        "HttpStatus: " + HttpStatus.FORBIDDEN + " ForbiddenActionException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidTokenException.class)
  public ErrorResponseDTO forbiddenAction(InvalidTokenException e) {
    logger.error(
        "HttpStatus: " + HttpStatus.BAD_REQUEST + " InvalidTokenException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InputMismatchException.class)
  public ErrorResponseDTO incorrectAmount(Exception e) {
    logger.error(
        "HttpStatus: " + HttpStatus.BAD_REQUEST + " InputMismatchException " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidTroopUpgradeException.class)
  public ErrorResponseDTO invalidUpgrade(Exception e) {
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST + " InvalidTroopUpgradeException "
        + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponseDTO methodArgumentNotValid(MethodArgumentNotValidException e) {
    var errors = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .collect(Collectors.joining(", "));
    logger.error("HttpStatus: 400 " + errors);
    return new ErrorResponseDTO(errors);
  }
}
