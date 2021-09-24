package com.greenfoxacademy.zelenamackatribes.chat.controllers;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackageClasses = {ChatController.class})
public class ChatControllerAdvice {

  private static Logger logger = LogManager.getLogger(ChatControllerAdvice.class);

  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ErrorResponseDTO requestBodyMissing() {
    String message = "Request body malformed or missing.";
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST
        + " HttpMessageNotReadableException: " + message);
    return new ErrorResponseDTO(message);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponseDTO someFieldMissing(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST + " " + message);
    return new ErrorResponseDTO(message);
  }
}
