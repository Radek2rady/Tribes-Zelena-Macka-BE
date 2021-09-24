package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

public class YmlReaderServiceImpl implements YmlReaderService {

  private String filename;
  private Map<String, Object> vals;

  public YmlReaderServiceImpl(String filename) throws DefaultValuesFileMissingException {
    this.filename = filename;
    init();
  }

  private void init() throws DefaultValuesFileMissingException {
    Yaml yaml = new Yaml();
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(new ClassPathResource(filename).getFile());
    } catch (IOException e) {
      throw new DefaultValuesFileMissingException();
    }
    vals = yaml.load(inputStream);
  }

  @Override
  public int getInt(String chain)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
    return (int) traverseMap(vals, getKeysTree(chain), Integer.class);
  }

  @Override
  public String getString(String chain)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
    return (String) traverseMap(vals, getKeysTree(chain), String.class);
  }

  @Override
  public List<String> getStringList(String chain)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
    var foundList = (ArrayList) traverseMap(vals, getKeysTree(chain), ArrayList.class);
    var outList = new ArrayList<String>(foundList.size());
    for (var item : foundList) {
      if (item instanceof String) {
        outList.add((String) item);
      } else {
        throw new IncorrectDefaultValueTypeException();
      }
    }
    return outList;
  }

  private static Object traverseMap(Object obj, List<String> keysTree, Class classType)
      throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
    if (obj instanceof HashMap) {
      Object subItem = ((HashMap<?, ?>) obj).get(keysTree.get(0));
      if (subItem == null) {
        throw new DefaultValueNotFoundException();
      }
      return traverseMap(subItem, keysTree.subList(1, keysTree.size()), classType);
    } else {
      if (obj.getClass() != classType) {
        throw new IncorrectDefaultValueTypeException();
      }
      return obj;
    }
  }

  private static List<String> getKeysTree(String chain) {
    return Arrays.asList(chain.split("\\."));
  }
}
