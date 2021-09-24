package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;

public class DefaultValuesFileMissingException extends DefaultValuesFileException {

  public DefaultValuesFileMissingException() {
    super("YAML file with values not found.");
  }
}
