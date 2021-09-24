package com.greenfoxacademy.zelenamackatribes.utils.integration;

import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TickServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SetEnvironmentVariable(
    key = "TRIBES_GAMETICK_LEN",
    value = "1"
)
public class GameTickTest {

  @MockBean
  private LeaderboardService leaderboardService;

  @MockBean
  private ResourceService resourceService;

  @Autowired
  private TickServiceImpl tickService;

  private boolean leaderboardServiceCalled = false;
  private boolean resourceServiceCalled = false;

  @Test
  public void scheduledCallShouldHappen() throws Exception {
    // environment variable TRIBES_GAMETICK_LEN is set to 1 second for this test
    Mockito.doAnswer(a -> {
      leaderboardServiceCalled = true;
      return null;
    }).when(leaderboardService).recountKingdomScores();
    Mockito.doAnswer(a -> {
      resourceServiceCalled = true;
      return null;
    }).when(resourceService).applyResourceGeneration();
    Thread.sleep(1100L);
    Assertions.assertTrue(tickService.getTickCounter() > 0);
    Assertions.assertTrue(leaderboardServiceCalled);
    Assertions.assertTrue(resourceServiceCalled);
  }
}
