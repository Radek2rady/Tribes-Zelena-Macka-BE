package com.greenfoxacademy.zelenamackatribes.users.exceptions;

public class InvalidUserCredentialsException extends Exception {

  public InvalidUserCredentialsException() {
    super("Username or password is incorrect.");
  }
}
