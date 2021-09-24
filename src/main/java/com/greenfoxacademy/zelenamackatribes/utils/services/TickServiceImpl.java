package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TickServiceImpl implements TickService {

  private static Logger logger = LogManager.getLogger(TimeServiceImpl.class);
  private final AtomicLong tickCounter = new AtomicLong(0);
  private final LeaderboardService leaderboardService;
  private final ResourceService resourceService;

  @Autowired
  public TickServiceImpl(LeaderboardService leaderboardService, ResourceService resourceService) {
    this.leaderboardService = leaderboardService;
    this.resourceService = resourceService;
  }

  @Override
  @Scheduled(fixedDelayString = "${TRIBES_GAMETICK_LEN}000")
  public void gameTick() throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException {
    tickCounter.incrementAndGet();
    leaderboardService.recountKingdomScores();
    resourceService.applyResourceGeneration();
    logger.debug("Game tick occurred");
  }

  public Long getTickCounter() {
    return tickCounter.get();
  }
}
