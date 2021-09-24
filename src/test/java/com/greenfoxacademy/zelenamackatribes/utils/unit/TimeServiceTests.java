package com.greenfoxacademy.zelenamackatribes.utils.unit;

import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeServiceImpl;
import java.time.Instant;
import java.time.temporal.ChronoField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimeServiceTests {

  private TimeService timeService = new TimeServiceImpl();

  private void waitIfNeeded() throws InterruptedException {
    var currentMillis = Instant.now().get(ChronoField.MILLI_OF_SECOND);
    if (currentMillis > 800) {
      Thread.sleep(1000 - currentMillis);
    }
  }

  @Test
  public void getTimeOk() throws InterruptedException {
    waitIfNeeded();
    Assertions.assertEquals(Instant.now().getEpochSecond(), timeService.getTime());
  }

  @Test
  public void getTimeAfterOk() throws InterruptedException {
    waitIfNeeded();
    var diff = 5457;
    Assertions.assertEquals(Instant.now().getEpochSecond() + diff, timeService.getTimeAfter(diff));
  }

  @Test
  public void getTimeBetweenOk() {
    var from = 1622742573L;
    var diff = 5457;
    var to = from + diff;
    Assertions.assertEquals(diff, timeService.getTimeBetween(from, to));
  }

  @Test
  public void getTimeBetweenWrongOrder() {
    var from = 1622742573L;
    var diff = 5457;
    var to = from - diff;
    var exception = Assertions
        .assertThrows(IllegalArgumentException.class, () -> timeService.getTimeBetween(from, to));
    Assertions.assertEquals("Value of 'from' must be less than 'to'", exception.getMessage());
  }

  @Test
  public void getTimeBetweenDifferenceTooBig() {
    var from = 1622742573L;
    var diff = 1L + Integer.MAX_VALUE;
    var to = from + diff;
    var exception = Assertions
        .assertThrows(ArithmeticException.class, () -> timeService.getTimeBetween(from, to));
    Assertions.assertEquals("Result too long", exception.getMessage());
  }
}
