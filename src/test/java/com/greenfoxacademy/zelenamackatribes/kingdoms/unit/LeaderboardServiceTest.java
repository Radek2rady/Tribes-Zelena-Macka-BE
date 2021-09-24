package com.greenfoxacademy.zelenamackatribes.kingdoms.unit;

import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.IncorrectPageParameterException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomScoreRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomServiceImpl;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardServiceImpl;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LeaderboardServiceTest {

  private BuildingService buildingService;
  private KingdomRepository kingdomRepository;
  private KingdomScoreRepository kingdomScoreRepository;
  private KingdomService kingdomService;
  private ResourceService resourceService;
  private TroopService troopService;
  private TimeService timeService;
  private LeaderboardService leaderboardService;
  private List<Kingdom> kingdoms;


  @BeforeEach
  public void init() {
    kingdomRepository = Mockito.mock(KingdomRepository.class);
    kingdomScoreRepository = Mockito.mock(KingdomScoreRepository.class);
    kingdomService = new KingdomServiceImpl(buildingService, kingdomRepository,
        kingdomScoreRepository, resourceService, troopService, leaderboardService);
    leaderboardService = new LeaderboardServiceImpl(buildingService, kingdomRepository,
        kingdomScoreRepository, resourceService, troopService, timeService);
    kingdoms = new ArrayList<>();
    kingdoms.add(new Kingdom());
    kingdoms.add(new Kingdom());
    kingdoms.add(new Kingdom());
  }

  @Test
  public void createLeaderboardThrowsNotEnoughKingdoms() {
    Mockito.when(kingdomRepository.findAll()).thenReturn(kingdoms);
    IncorrectPageParameterException exception = Assertions.assertThrows(
        IncorrectPageParameterException.class,
        () -> leaderboardService.createLeaderboard(3, 2, "totalScore", false));
    Assertions.assertEquals("Not enough kingdoms to display given pageNo",
        exception.getMessage());
  }

  @Test
  public void createLeaderboardThrowsIncorrectPageNo() {
    Mockito.when(kingdomRepository.findAll()).thenReturn(kingdoms);
    IncorrectPageParameterException exception = Assertions.assertThrows(
        IncorrectPageParameterException.class,
        () -> leaderboardService.createLeaderboard(-1, 2, "totalScore", true));
    Assertions.assertEquals("Parameter pageNo must be a positive integer",
        exception.getMessage());
  }

  @Test
  public void createLeaderboardThrowsIncorrectPageSize() {
    Mockito.when(kingdomRepository.findAll()).thenReturn(kingdoms);
    IncorrectPageParameterException exception = Assertions.assertThrows(
        IncorrectPageParameterException.class,
        () -> leaderboardService.createLeaderboard(0, 0, "totalScore", false));
    Assertions.assertEquals("Parameter pageSize must be a positive integer",
        exception.getMessage());
  }

  @Test
  public void createLeaderboardThrowsIncorrectScoreType() {
    Mockito.when(kingdomRepository.findAll()).thenReturn(kingdoms);
    IncorrectPageParameterException exception = Assertions.assertThrows(
        IncorrectPageParameterException.class,
        () -> leaderboardService.createLeaderboard(0, 5, "ttalSore", true));
    Assertions.assertEquals("Parameter scoreType is not of a given type",
        exception.getMessage());
  }
}
