package com.greenfoxacademy.zelenamackatribes.troops.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidAcademyException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Academy;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Farm;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.InvalidTroopUpgradeException;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.TroopNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.repositories.TroopRepository;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopServiceImpl;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TroopServiceTest {

  private TroopRepository troopRepository;
  private TroopService troopService;
  private TimeService timeService;
  private ResourceService resourceService;
  private Kingdom testKingdom;
  private Kingdom otherKingdom;
  private Building academy;

  @BeforeEach
  public void init() throws Exception {
    this.troopRepository = Mockito.mock(TroopRepository.class);
    this.timeService = Mockito.mock(TimeService.class);
    this.resourceService = Mockito.mock(ResourceService.class);
    this.troopService = new TroopServiceImpl(troopRepository, timeService, resourceService);
    testKingdom = new Kingdom(1L, null, null, null, null, null, null);
    otherKingdom = new Kingdom(2L, null, null, null, null, null, null);
    academy = new Academy();
    academy.setKingdom(testKingdom);
    academy.setFinishedAt(0L);
  }

  @Test
  public void createTroopOk() throws Exception {
    Mockito.doNothing().when(resourceService).handlePurchase(testKingdom, 25, 0);

    Troop troop = troopService.createTroop(academy);
    Assertions.assertEquals(1, troop.getLevel());
    Assertions.assertEquals(20, troop.getHp());
  }

  @Test
  public void createTroopIncorrectBuildingType() throws Exception {
    Building otherBuilding = new Farm();
    otherBuilding.setKingdom(testKingdom);

    Mockito.doNothing().when(resourceService).handlePurchase(testKingdom, 25, 0);

    InvalidAcademyException exception = Assertions.assertThrows(InvalidAcademyException.class,
        () -> troopService.createTroop(otherBuilding));
    Assertions.assertEquals("Not a valid academy id", exception.getMessage());
  }

  @Test
  public void createTroopNotEnoughResources() throws Exception {
    Mockito.doThrow(new NotEnoughResourcesException("Don't have enough gold"))
        .when(resourceService).handlePurchase(testKingdom, 25, 0);

    NotEnoughResourcesException exception = Assertions
        .assertThrows(NotEnoughResourcesException.class,
            () -> troopService.createTroop(academy));
    Assertions.assertEquals("Don't have enough gold", exception.getMessage());
  }

  @Test
  public void getTroopByIdThrowsTroopNotFoundException() {
    Mockito.when(troopRepository.findById(any())).thenReturn(Optional.empty());

    TroopNotFoundException exception = Assertions.assertThrows(TroopNotFoundException.class,
        () -> troopService.getTroopById(1L));
    Assertions.assertEquals("Troop id not found", exception.getMessage());
  }

  @Test
  public void getTroopByIdReturnsOk() throws Exception {
    Troop troop = new Troop();

    Mockito.when(troopRepository.findById(any()))
        .thenReturn(Optional.of(troop));

    Assertions.assertEquals(troop, troopService.getTroopById(1L));
  }

  @Test
  public void getTroopThrowsForbiddenActionException() {
    Troop troop = new Troop();
    troop.setKingdom(testKingdom);

    Mockito.when(troopRepository.findById(any())).thenReturn(Optional.of(troop));

    ForbiddenActionException exception = Assertions.assertThrows(ForbiddenActionException.class,
        () -> troopService.getTroop(1L, 2L));
    Assertions.assertEquals("Forbidden action", exception.getMessage());
  }

  @Test
  public void getTroopReturnsTroopObject() throws Exception {
    Troop troop = new Troop();
    troop.setKingdom(testKingdom);

    Mockito.when(troopRepository.findById(any())).thenReturn(Optional.of(troop));

    Assertions.assertEquals(troop, troopService.getTroop(1L, 1L));
  }

  @Test
  public void upgradeThrowsWithUnownedTroop() {
    var troop = new Troop();
    troop.setKingdom(otherKingdom);

    var exception = Assertions.assertThrows(ForbiddenActionException.class,
        () -> troopService.upgrade(testKingdom, troop, academy));
    Assertions.assertEquals("Forbidden action", exception.getMessage());
  }

  @Test
  public void upgradeThrowsWithUnownedAcademy() {
    var troop = new Troop();
    troop.setKingdom(otherKingdom);

    var exception = Assertions.assertThrows(ForbiddenActionException.class,
        () -> troopService.upgrade(otherKingdom, troop, academy));
    Assertions.assertEquals("Forbidden action", exception.getMessage());
  }

  @Test
  public void upgradeThrowsWithWrongBuildingType() throws Exception {
    var troop = new Troop();
    troop.setKingdom(testKingdom);
    var building = new Farm();
    building.setKingdom(testKingdom);

    var exception = Assertions.assertThrows(InvalidAcademyException.class,
        () -> troopService.upgrade(testKingdom, troop, building));
    Assertions.assertEquals("Invalid academy id", exception.getMessage());
  }

  @Test
  public void upgradeThrowsWithUnfinishedAcademy() {
    var troop = new Troop();
    troop.setKingdom(testKingdom);
    academy.setFinishedAt(1234567L);

    Mockito.when(timeService.getTime()).thenReturn(123456L);

    var exception = Assertions.assertThrows(InvalidAcademyException.class,
        () -> troopService.upgrade(testKingdom, troop, academy));
    Assertions.assertEquals("Cannot use unfinished academy for upgrade", exception.getMessage());
  }

  @Test
  public void upgradeThrowsWithTroopLevelOverAcademy() {
    var troop = new Troop();
    troop.setLevel(1);
    academy.setLevel(1);
    troop.setKingdom(testKingdom);

    Mockito.when(timeService.getTime()).thenReturn(123456L);

    var exception = Assertions.assertThrows(InvalidTroopUpgradeException.class,
        () -> troopService.upgrade(testKingdom, troop, academy));
    Assertions.assertEquals("Cannot upgrade troop above academy level", exception.getMessage());
  }

  @Test
  public void upgradeThrowsWithFullAcademy() {
    var troop = new Troop();
    troop.setLevel(1);
    troop.setKingdom(testKingdom);
    academy.setLevel(2);

    Mockito.when(timeService.getTime()).thenReturn(123456L);
    Mockito.when(troopRepository.countAllByAcademyAndFinishedAtGreaterThan(any(), any()))
        .thenReturn(5L);

    var exception = Assertions.assertThrows(NotEnoughResourcesException.class,
        () -> troopService.upgrade(testKingdom, troop, academy));
    Assertions.assertEquals("Not enough space in academy", exception.getMessage());
  }

  @Test
  public void upgradeThrowsWithoutEnoughResources() throws Exception {
    var troop = new Troop();
    troop.setLevel(1);
    troop.setKingdom(testKingdom);
    academy.setLevel(2);

    Mockito.when(timeService.getTime()).thenReturn(123456L);
    Mockito.when(troopRepository.countAllByAcademyAndFinishedAtGreaterThan(any(), any()))
        .thenReturn(0L);
    Mockito.doThrow(new NotEnoughResourcesException("mock message")).when(resourceService)
        .handlePurchase(any(), anyInt(), anyInt());

    var exception = Assertions.assertThrows(NotEnoughResourcesException.class,
        () -> troopService.upgrade(testKingdom, troop, academy));
    Assertions.assertEquals("mock message", exception.getMessage());
  }

  @Test
  public void upgradeOK() throws Exception {
    var troop = new Troop();
    troop.setLevel(1);
    troop.setKingdom(testKingdom);
    academy.setLevel(5);

    Mockito.when(timeService.getTime()).thenReturn(123456L);
    Mockito.when(timeService.getTimeAfter(anyInt())).thenReturn(456789L);
    Mockito.when(troopRepository.countAllByAcademyAndFinishedAtGreaterThan(any(), any()))
        .thenReturn(0L);
    Mockito.doNothing().when(resourceService).handlePurchase(any(), anyInt(), anyInt());

    troop = troopService.upgrade(testKingdom, troop, academy);
    Assertions.assertEquals(academy, troop.getAcademy());
    Assertions.assertEquals(123456L, troop.getStartedAt());
    Assertions.assertEquals(456789L, troop.getFinishedAt());
    Assertions.assertEquals(5, troop.getLevel());
    Assertions.assertEquals(100, troop.getHp());
    Assertions.assertEquals(50, troop.getAttack());
    Assertions.assertEquals(25, troop.getDefence());
  }
}
