package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;

public class IncorrectDefaultValueTypeException extends DefaultValuesFileException {

  public IncorrectDefaultValueTypeException() {
    super(
        "Incorrect value type found, only non-decimal numbers are allowed to be stored in config "
            + "file");
  }

}
