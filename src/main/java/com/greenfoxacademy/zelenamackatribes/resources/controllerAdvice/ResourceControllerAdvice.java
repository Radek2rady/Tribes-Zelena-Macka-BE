package com.greenfoxacademy.zelenamackatribes.resources.controllerAdvice;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.controllers.ResourceController;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {ResourceController.class})
public class ResourceControllerAdvice {

  private static Logger logger = LogManager.getLogger(ResourceControllerAdvice.class);

  @ResponseStatus(HttpStatus.NOT_FOUND) //404
  @ExceptionHandler(InvalidNumberOfResourceObjectsException.class)
  public ErrorResponseDTO resourcesNotFound(InvalidNumberOfResourceObjectsException e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND) //404
  @ExceptionHandler(KingdomNotFoundException.class)
  public ErrorResponseDTO kingdomNotFound(KingdomNotFoundException e) {
    logger.error("HttpStatus: 404" + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST) //400
  @ExceptionHandler(InvalidTokenException.class)
  public ErrorResponseDTO invalidToken(Exception e) {
    logger.error("HttpStatus: 400 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND) //404
  @ExceptionHandler(ResourceNotFoundException.class)
  public ErrorResponseDTO resourceNotFound(ResourceNotFoundException e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND) //404
  @ExceptionHandler(NotEnoughResourcesException.class)
  public ErrorResponseDTO notEnoughResources(NotEnoughResourcesException e) {
    logger.error("HttpStatus: 404 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }
}
