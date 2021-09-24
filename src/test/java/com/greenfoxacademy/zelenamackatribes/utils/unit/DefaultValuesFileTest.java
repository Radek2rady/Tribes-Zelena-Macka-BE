package com.greenfoxacademy.zelenamackatribes.utils.unit;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import com.greenfoxacademy.zelenamackatribes.utils.services.YmlReaderService;
import com.greenfoxacademy.zelenamackatribes.utils.services.YmlReaderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@TestMethodOrder(OrderAnnotation.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({DefaultVals.class})
public class DefaultValuesFileTest {

  @BeforeEach
  public void init() throws DefaultValuesFileMissingException {
    YmlReaderService ymlReaderService = new YmlReaderServiceImpl("testValues.yml");
    Whitebox.setInternalState(DefaultVals.class, "ymlReaderService", ymlReaderService);
  }

  @AfterEach
  public void repairSingleton() throws DefaultValuesFileMissingException {
    Whitebox.setInternalState(
        DefaultVals.class,
        "ymlReaderService",
        new YmlReaderServiceImpl("values.yml")
    );
  }

  @Test
  @Order(1)
  public void queryNotExistingValue() {
    Assertions.assertThrows(
        DefaultValueNotFoundException.class,
        () -> DefaultVals.getInt("non.existing.value")
    );
  }

  @Test
  @Order(2)
  public void queryWrongType() {
    Assertions.assertThrows(
        IncorrectDefaultValueTypeException.class,
        () -> DefaultVals.getString("test.value1")
    );
  }

  @Test
  @Order(3)
  public void queryValue()
      throws DefaultValuesFileMissingException, DefaultValueNotFoundException,
      IncorrectDefaultValueTypeException {
    Assertions.assertEquals(DefaultVals.getInt("test.value1"), 100);
  }

  @Test
  @Order(4)
  public void fileIsMissing() {
    Assertions.assertThrows(
        DefaultValuesFileMissingException.class,
        () -> new YmlReaderServiceImpl("nonExisting.yml")
    );
  }
}
