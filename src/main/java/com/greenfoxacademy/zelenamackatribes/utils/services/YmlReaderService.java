package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import java.util.List;

public interface YmlReaderService {

  int getInt(String chain) throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException;

  String getString(String chain)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException;

  List<String> getStringList(String chain)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException;
}
