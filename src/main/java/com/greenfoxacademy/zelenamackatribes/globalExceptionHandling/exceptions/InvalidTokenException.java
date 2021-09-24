package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions;

import io.jsonwebtoken.JwtException;

public class InvalidTokenException extends JwtException {

  public InvalidTokenException(String message) {
    super(message);
  }
}
