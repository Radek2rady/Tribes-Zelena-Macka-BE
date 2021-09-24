package com.greenfoxacademy.zelenamackatribes.utils.other;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import com.greenfoxacademy.zelenamackatribes.utils.services.YmlReaderService;
import com.greenfoxacademy.zelenamackatribes.utils.services.YmlReaderServiceImpl;
import java.util.List;

public class DefaultVals {

  private static DefaultVals defaultVals;
  private static YmlReaderService ymlReaderService;

  private DefaultVals() {
  }

  private DefaultVals(YmlReaderService reader) {
    ymlReaderService = reader;
  }

  public static DefaultVals getInstance() throws DefaultValuesFileMissingException {
    if (defaultVals == null) {
      defaultVals = new DefaultVals(new YmlReaderServiceImpl("values.yml"));
    }
    return defaultVals;
  }

  public static int getInt(String chain)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException,
      DefaultValuesFileMissingException {
    getInstance();
    return ymlReaderService.getInt(chain);
  }

  public static String getString(String chain)
      throws DefaultValuesFileMissingException, DefaultValueNotFoundException,
      IncorrectDefaultValueTypeException {
    getInstance();
    return ymlReaderService.getString(chain);
  }

  public static List<String> getStringList(String chain)
      throws DefaultValuesFileMissingException, DefaultValueNotFoundException,
      IncorrectDefaultValueTypeException {
    getInstance();
    return ymlReaderService.getStringList(chain);
  }
}
