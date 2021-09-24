package com.greenfoxacademy.zelenamackatribes.kingdoms.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Academy;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Farm;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Mine;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Townhall;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomScoreRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomServiceImpl;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeServiceImpl;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class KingdomServiceCreateKingdomTest {

  private BuildingService buildingService;
  private KingdomRepository kingdomRepository;
  private KingdomScoreRepository kingdomScoreRepository;
  private TimeService timeService;
  private KingdomService kingdomService;
  private ResourceService resourceService;
  private TroopService troopService;
  private LeaderboardService leaderboardService;

  private UserEntity user;

  @BeforeEach
  public void init() throws Exception {
    buildingService = Mockito.mock(BuildingService.class);
    troopService = Mockito.mock(TroopService.class);
    kingdomRepository = Mockito.mock(KingdomRepository.class);
    kingdomScoreRepository = Mockito.mock(KingdomScoreRepository.class);
    resourceService = Mockito.mock(ResourceService.class);
    troopService = Mockito.mock(TroopService.class);
    leaderboardService = Mockito.mock(LeaderboardService.class);
    timeService = new TimeServiceImpl();
    kingdomService = new KingdomServiceImpl(buildingService, kingdomRepository,
        kingdomScoreRepository, resourceService, troopService, leaderboardService);
    user = new UserEntity();

    Mockito.when(kingdomRepository.save(Mockito.any())).thenAnswer(a -> a.getArgument(0));
    Mockito.when(kingdomScoreRepository.save(Mockito.any())).thenAnswer(a -> a.getArgument(0));
    Mockito.when(resourceService.getResources(Mockito.any()))
        .thenAnswer(a -> a.getArgument(0, Kingdom.class).getResources());
  }

  @Test
  public void newKingdomCreatedWithResourcesAndBuildings() throws Exception {
    Mockito.when(buildingService.createBuilding(eq(BuildingType.TOWNHALL), any(), eq(true)))
        .thenAnswer((a) -> {
          var b = new Townhall();
          b.setFinishedAt(timeService.getTime());
          a.getArgument(1, Kingdom.class).getBuildings().add(b);
          return b;
        });
    Mockito.when(buildingService.createBuilding(eq(BuildingType.ACADEMY), any(), eq(true)))
        .thenAnswer((a) -> {
          var b = new Academy();
          b.setFinishedAt(timeService.getTime());
          a.getArgument(1, Kingdom.class).getBuildings().add(b);
          return b;
        });
    Mockito.when(buildingService.createBuilding(eq(BuildingType.FARM), any(), eq(true)))
        .thenAnswer((a) -> {
          var b = new Farm();
          b.setFinishedAt(timeService.getTime());
          a.getArgument(1, Kingdom.class).getBuildings().add(b);
          return b;
        });
    Mockito.when(buildingService.createBuilding(eq(BuildingType.MINE), any(), eq(true)))
        .thenAnswer((a) -> {
          var b = new Mine();
          b.setFinishedAt(timeService.getTime());
          a.getArgument(1, Kingdom.class).getBuildings().add(b);
          return b;
        });

    var kingdom = kingdomService.createKingdom(user, "Some kingdom");

    Assertions.assertEquals("Some kingdom", kingdom.getKingdomName());
    Assertions.assertIterableEquals(
        Arrays.asList(new Resource(null, ResourceType.GOLD, 100, kingdom, 20, 0L),
            new Resource(null, ResourceType.FOOD, 50, kingdom, 20, 0L)),
        kingdom.getResources());
    Assertions.assertTrue(
        kingdom.getBuildings().stream().allMatch(b -> b.getFinishedAt() <= timeService.getTime()));
    Assertions.assertEquals(1,
        kingdom.getBuildings().stream().filter(b -> b.getType() == BuildingType.TOWNHALL).count());
    Assertions.assertEquals(1,
        kingdom.getBuildings().stream().filter(b -> b.getType() == BuildingType.ACADEMY).count());
    Assertions.assertEquals(1,
        kingdom.getBuildings().stream().filter(b -> b.getType() == BuildingType.FARM).count());
    Assertions.assertEquals(1,
        kingdom.getBuildings().stream().filter(b -> b.getType() == BuildingType.MINE).count());
  }

  @Test
  public void throwsWhenBuildingCreationFails() throws Exception {
    Mockito
        .when(buildingService.createBuilding(eq(BuildingType.TOWNHALL), any(), eq(true)))
        .thenThrow(InvalidBuildingTypeException.class);
    Assertions.assertThrows(
        InvalidBuildingTypeException.class,
        () -> kingdomService.createKingdom(user, "Some kingdom")
    );

    Mockito
        .when(buildingService.createBuilding(eq(BuildingType.TOWNHALL), any(), eq(true)))
        .thenThrow(NotEnoughResourcesException.class);
    Assertions.assertThrows(
        NotEnoughResourcesException.class,
        () -> kingdomService.createKingdom(user, "Some kingdom")
    );
  }
}