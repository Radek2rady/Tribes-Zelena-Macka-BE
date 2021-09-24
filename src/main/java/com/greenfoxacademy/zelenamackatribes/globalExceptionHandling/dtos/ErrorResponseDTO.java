package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos;

import lombok.Getter;

@Getter
public class ErrorResponseDTO extends ResponseDTO {

  private String message;

  public ErrorResponseDTO(String message) {
    status = "error";
    this.message = message;
  }
}
