package com.greenfoxacademy.zelenamackatribes.buildings.exceptions;

import lombok.Getter;

@Getter
public class MissingParamException extends Exception {

  public MissingParamException(String message) {
    super(message);
  }
}
