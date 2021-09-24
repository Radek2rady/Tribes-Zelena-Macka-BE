package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;

public class DefaultValueNotFoundException extends DefaultValuesFileException {

  public DefaultValueNotFoundException() {
    super("Given value not found.");
  }
}
