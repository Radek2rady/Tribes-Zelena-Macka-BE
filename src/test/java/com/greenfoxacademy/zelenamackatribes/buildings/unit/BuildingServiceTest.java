package com.greenfoxacademy.zelenamackatribes.buildings.unit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingUpgradeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MaximumLevelReachedException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingFactory;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Academy;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Townhall;
import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingServiceImpl;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.models.resources.Gold;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildingServiceTest {

  private BuildingService buildingService;
  private BuildingRepository buildingRepository;
  private TimeService timeService;
  private KingdomService kingdomService;
  private ResourceService resourceService;

  @BeforeEach
  public void setup() {
    buildingRepository = Mockito.mock(BuildingRepository.class);
    timeService = Mockito.mock(TimeService.class);
    resourceService = Mockito.mock(ResourceService.class);
    buildingService = new BuildingServiceImpl(buildingRepository, resourceService, timeService);
    kingdomService = Mockito.mock(KingdomService.class);
  }

  @Test
  void createBuildingShouldReturnBuildingObject()
      throws Exception {
    final Kingdom fakeKingdom = new Kingdom(null, null, null,
        new ArrayList<>(), null, null, null);
    Gold gold = new Gold();
    gold.setAmount(200);

    Mockito.when(resourceService.getGold(any())).thenReturn(gold);

    Building buildingTest = buildingService.createBuilding("townhall", fakeKingdom);
    assertNotNull(buildingTest);
  }

  @Test
  void createBuildingShouldReturnNotEnoughResourceException() throws Exception {
    final Kingdom fakeKingdom = new Kingdom(null, null, null,
        Collections.emptyList(), null, null, null);
    Gold gold = new Gold();
    gold.setAmount(0);

    Mockito.doThrow(new NotEnoughResourcesException("Don't have enough gold"))
        .when(resourceService).handlePurchase(any(), anyInt(), anyInt());

    Mockito.when(resourceService.getGold(any())).thenReturn(gold);
    NotEnoughResourcesException exception = Assertions
        .assertThrows(NotEnoughResourcesException.class,
            () -> {
              buildingService.createBuilding("townhall", fakeKingdom);
            });
    Assertions.assertEquals("Don't have enough gold", exception.getMessage());
  }

  @Test
  void createBuildingShouldReturnInvalidBuildingTypeExceptionWhenKingdomHasNoTownhall() {
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", null, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), null);

    InvalidBuildingTypeException exception = Assertions
        .assertThrows(InvalidBuildingTypeException.class,
            () -> {
              buildingService.createBuilding("academy", fakeKingdom);
            });
    Assertions.assertEquals("The kingdom has no townhall",
        exception.getMessage());
  }

  @Test
  void createBuildingShouldReturnInvalidBuildingTypeException() throws DefaultValuesFileException {
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", null, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), null);
    fakeKingdom.setResources(
        Collections.singletonList(new Resource(1L, ResourceType.GOLD, 200, fakeKingdom)));
    fakeKingdom.setBuildings(Collections.singletonList(new Townhall()));

    InvalidBuildingTypeException exception = Assertions
        .assertThrows(InvalidBuildingTypeException.class,
            () -> {
              buildingService.createBuilding("notExistingBuilding", fakeKingdom);
            });
    Assertions.assertEquals("Invalid building type", exception.getMessage());
  }

  @Test
  void ifEnoughGoldThenShouldCreateBuildingObject() throws Exception {
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", null, new ArrayList<>(
        Arrays.asList(new Townhall())), null, null, null);

    Mockito.when(buildingRepository.findAllByKingdom(fakeKingdom))
        .thenReturn(Arrays.asList(new Townhall()));
    Mockito.doNothing().when(resourceService).handlePurchase(any(),
        anyInt(), anyInt());

    Building building = buildingService.createBuilding("academy", fakeKingdom);
    assertNotNull(building);
  }

  @Test
  void ifNotEnoughGoldThenShouldThrowNotEnoughResourceException() throws Exception {
    final Kingdom fakeKingdom = new Kingdom(1L, null, null,
        Collections.emptyList(), null, null, null);

    Mockito.doThrow(NotEnoughResourcesException.class).when(resourceService)
        .handlePurchase(fakeKingdom,
            BuildingFactory.getPrice(BuildingType.TOWNHALL), 0);

    Assertions.assertThrows(NotEnoughResourcesException.class,
        () -> {
          buildingService.createBuilding("townhall", fakeKingdom);
        });
  }

  @Test
  public void findBuildingByIdOk() throws InvalidBuildingIdException, DefaultValuesFileException {
    Optional<Building> academy = Optional.of(new Academy());
    Mockito.when(buildingRepository.findById(any())).thenReturn(academy);

    Building result = buildingService.getBuildingById(15L);
    Assertions.assertEquals(academy.get().getId(), result.getId());
  }

  @Test
  public void findBuildingByIdNotFound() {
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.empty());

    InvalidBuildingIdException exception = Assertions
        .assertThrows(InvalidBuildingIdException.class,
            () -> buildingService.getBuildingById(100L));
    Assertions.assertEquals("Building id not found", exception.getMessage());
  }

  @Test
  public void getBuildingsForKingdomOk() throws DefaultValuesFileException {
    var buildings = Arrays.asList(new Townhall(), new Academy());
    Mockito.when(buildingRepository.findAllByKingdom(any())).thenReturn(buildings);

    var found = buildingService.getBuildingsForKingdom(new Kingdom());
    Assertions.assertIterableEquals(buildings, found);
  }

  @Test
  public void getBuildingForKingdomThrowsOnKingdomMismatch() throws DefaultValuesFileException {
    var kingdom1 = new Kingdom();
    kingdom1.setId(1L);
    var kingdom2 = new Kingdom();
    kingdom2.setId(2L);
    var building = new Townhall();
    building.setKingdom(kingdom1);
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.of(building));

    var exception = Assertions.assertThrows(ForbiddenActionException.class,
        () -> buildingService.getBuildingForKingdom(100L, kingdom2));
    Assertions.assertEquals("Forbidden action", exception.getMessage());
  }

  @Test
  public void getBuildingForKingdomOk() throws Exception {
    var kingdom = new Kingdom();
    kingdom.setId(1L);
    var building = new Townhall();
    building.setKingdom(kingdom);
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.of(building));

    var found = buildingService.getBuildingForKingdom(100L, kingdom);
    Assertions.assertEquals(building, found);
  }

  @Test
  public void upgradeBuildingThrowNoGold()
      throws DefaultValuesFileException, NotEnoughResourcesException,
      InvalidNumberOfResourceObjectsException, ResourceNotFoundException,
      InvalidBuildingTypeException {
    UserEntity johnyWalker = new UserEntity();
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", johnyWalker,
        null, null, null, null);
    fakeKingdom.setResources(
        Collections.singletonList(new Resource(1L, ResourceType.GOLD, 5, fakeKingdom)));
    Building academy = new Academy();
    academy.setId(4L);
    academy.setKingdom(fakeKingdom);
    Townhall townhall = new Townhall();
    townhall.setKingdom(fakeKingdom);
    townhall.setId(1L);
    townhall.setLevel(5);
    fakeKingdom.setBuildings(Arrays.asList(townhall, academy));
    johnyWalker.setKingdom(fakeKingdom);
    Gold gold = new Gold();
    gold.setAmount(0);
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.of(academy));
    Mockito.when(resourceService.getGold(any())).thenReturn(gold);

    Assertions.assertThrows(NotEnoughResourcesException.class,
        () -> {
          buildingService.upgradeBuilding(fakeKingdom, academy.getId());
        });
  }

  @Test
  public void upgradeBuildingLevelNotLowerThanTownhall() throws Exception {
    UserEntity johnyWalker = new UserEntity();
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", johnyWalker,
        null, null, null, null);
    fakeKingdom.setResources(
        Collections.singletonList(new Resource(1L, ResourceType.GOLD, 200, fakeKingdom)));
    Building academy = new Academy();
    academy.setId(4L);
    academy.setKingdom(fakeKingdom);
    Townhall townhall = new Townhall();
    townhall.setId(1L);
    townhall.setLevel(1);
    fakeKingdom.setBuildings(Arrays.asList(townhall, academy));
    johnyWalker.setKingdom(fakeKingdom);
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.of(academy));
    Mockito.when(resourceService.getGold(any())).thenReturn(new Gold());
    Assertions.assertThrows(InvalidBuildingUpgradeException.class,
        () -> {
          buildingService.upgradeBuilding(fakeKingdom, academy.getId());
        });
  }

  @Test
  public void upgradeBuildingMaxLevelException() throws Exception {
    UserEntity johnyWalker = new UserEntity();
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", johnyWalker,
        null, null, null, null);
    Gold gold = new Gold();
    gold.setAmount(200);
    Mockito.when(resourceService.getGold(any())).thenReturn(gold);
    Townhall townhall = new Townhall();
    townhall.setId(1L);
    townhall.setLevel(20);
    fakeKingdom.setBuildings(Arrays.asList(townhall));
    johnyWalker.setKingdom(fakeKingdom);
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.of(townhall));
    fakeKingdom.setResources(
        Collections.singletonList(new Resource(1L, ResourceType.GOLD, 200, fakeKingdom)));
    Assertions.assertThrows(MaximumLevelReachedException.class,
        () -> {
          buildingService.upgradeBuilding(fakeKingdom, townhall.getId());
        });
  }

  @Test
  public void upgradeBuildingNotReady() throws Exception {
    UserEntity johnyWalker = new UserEntity();
    final Kingdom fakeKingdom = new Kingdom(1L, "fakeKingdom", johnyWalker,
        null, null, null, null);
    Gold gold = new Gold();
    gold.setAmount(200);
    Mockito.when(resourceService.getGold(any())).thenReturn(new Gold());
    Townhall townhall = new Townhall();
    townhall.setId(1L);
    var finishedTime = townhall.getFinishedAt();
    townhall.setFinishedAt(finishedTime - 100);
    fakeKingdom.setBuildings(Arrays.asList(townhall));
    johnyWalker.setKingdom(fakeKingdom);
    Mockito.when(buildingRepository.findById(any())).thenReturn(Optional.of(townhall));
    Mockito.when(resourceService.getGold(any())).thenReturn(gold);
    Assertions.assertThrows(InvalidBuildingUpgradeException.class,
        () -> {
          buildingService.upgradeBuilding(fakeKingdom, townhall.getId());
        });
  }
}
