package com.greenfoxacademy.zelenamackatribes.buildings.services;

import com.google.common.base.Enums;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.BuildingIdNotBelongToKingdomException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingUpgradeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MaximumLevelReachedException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingFactory;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Townhall;
import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.InputMismatchException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildingServiceImpl implements BuildingService {

  private final BuildingRepository buildingRepository;
  private final ResourceService resourceService;
  private final TimeService timeService;

  @Autowired
  public BuildingServiceImpl(
      BuildingRepository buildingRepository, ResourceService resourceService,
      TimeService timeService) {
    this.buildingRepository = buildingRepository;
    this.resourceService = resourceService;
    this.timeService = timeService;
  }

  @Override
  public Building createBuilding(String buildingType, Kingdom kingdom)
      throws InvalidBuildingTypeException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException, InputMismatchException,
      DefaultValuesFileException {
    BuildingType type = convertStringToBuildingType(buildingType.toUpperCase());
    return createBuilding(type, kingdom);
  }

  @Override
  public Building createBuilding(BuildingType buildingType,
      Kingdom kingdom)
      throws InvalidBuildingTypeException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException, InputMismatchException,
      DefaultValuesFileException {
    return createBuilding(buildingType, kingdom, false);
  }

  @Override
  public Building createBuilding(BuildingType buildingType, Kingdom kingdom,
      boolean starterBuilding)
      throws InvalidBuildingTypeException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException, InputMismatchException,
      DefaultValuesFileException {
    if (!buildingType.equals(BuildingType.TOWNHALL) && !hasTownhall(kingdom)) {
      throw new InvalidBuildingTypeException(
          "The kingdom has no townhall");
    }
    if (buildingType.equals(BuildingType.TOWNHALL) && hasTownhall(kingdom)) {
      throw new InvalidBuildingTypeException(
          "Kingdom already has a Townhall, cannot build another Townhall");
    }
    if (!starterBuilding) {
      resourceService.handlePurchase(kingdom, BuildingFactory.getPrice(buildingType), 0);
    }
    Building newBuilding = BuildingFactory.createBuilding(buildingType);
    var finishedAt = newBuilding.getFinishedAt();
    if (starterBuilding) {
      var time = timeService.getTime();
      newBuilding.setFinishedAt(time);
      finishedAt = time;
    }
    newBuilding.setKingdom(kingdom);
    buildingRepository.save(newBuilding);
    switch (buildingType) {
      case TOWNHALL:
        resourceService.addGenerationChange(kingdom, ResourceType.FOOD,
            DefaultVals.getInt("buildings.townhall.food"), finishedAt);
        resourceService.addGenerationChange(kingdom, ResourceType.GOLD,
            DefaultVals.getInt("buildings.townhall.gold"), finishedAt);
        break;
      case FARM:
        resourceService.addGenerationChange(kingdom, ResourceType.FOOD,
            DefaultVals.getInt("buildings.farm.food.base")
                + DefaultVals.getInt("buildings.farm.food.perLevel"), finishedAt);
        break;
      case MINE:
        resourceService.addGenerationChange(kingdom, ResourceType.GOLD,
            DefaultVals.getInt("buildings.mine.gold.base")
                + DefaultVals.getInt("buildings.mine.gold.perLevel"), finishedAt);
        break;
      default:
        break;
    }
    kingdom.getBuildings().add(newBuilding);
    return newBuilding;
  }

  @Override
  public boolean checkIfEnoughGoldToCreateBuilding(int gold, BuildingType type)
      throws InvalidBuildingTypeException, DefaultValuesFileException {
    return gold >= BuildingFactory.getPrice(type);
  }

  @Override
  public boolean hasTownhall(Kingdom kingdom) {
    return kingdom
        .getBuildings()
        .stream()
        .anyMatch(building -> building instanceof Townhall);
  }

  @Override
  public Building getBuildingById(Long buildingId) throws InvalidBuildingIdException {
    return buildingRepository.findById(buildingId)
        .orElseThrow(() -> new InvalidBuildingIdException("Building id not found"));
  }

  @Override
  public List<Building> getBuildingsForKingdom(Kingdom kingdom) {
    return buildingRepository.findAllByKingdom(kingdom);
  }

  @Override
  public Building getBuildingForKingdom(Long buildingId, Kingdom kingdom)
      throws InvalidBuildingIdException, ForbiddenActionException {
    var building = getBuildingById(buildingId);
    if (building.getKingdom().getId().equals(kingdom.getId())) {
      return building;
    } else {
      throw new ForbiddenActionException("Forbidden action");
    }
  }

  @Override
  public Building upgradeBuilding(Kingdom kingdom, Long buildingId)
      throws NotEnoughResourcesException, InvalidBuildingTypeException,
      MaximumLevelReachedException, InvalidBuildingUpgradeException,
      InvalidBuildingIdException, BuildingIdNotBelongToKingdomException, ResourceNotFoundException,
      DefaultValuesFileException, InvalidNumberOfResourceObjectsException {
    Building upgradedBuilding = getBuildingById(buildingId);
    validateBuildingUpgrade(buildingId, kingdom);
    var buildingType = upgradedBuilding.getType();
    resourceService.handlePurchase(kingdom, BuildingFactory.getPrice(buildingType), 0);
    Integer level = upgradedBuilding.getLevel();
    upgradedBuilding.setLevel(level + 1);
    var finishedAt = 0L;
    switch (buildingType) {
      case TOWNHALL:
        finishedAt = timeService.getTimeAfter(
            DefaultVals.getInt("buildings.townhall.defaultBuildingTime"));
        upgradedBuilding.setFinishedAt(finishedAt);
        resourceService.addGenerationChange(kingdom, ResourceType.FOOD,
            DefaultVals.getInt("buildings.townhall.food"), finishedAt);
        resourceService.addGenerationChange(kingdom, ResourceType.GOLD,
            DefaultVals.getInt("buildings.townhall.gold"), finishedAt);
        break;
      case ACADEMY:
        upgradedBuilding.setFinishedAt(
            timeService.getTimeAfter(DefaultVals.getInt("buildings.academy.defaultBuildingTime")));
        break;
      case FARM:
        finishedAt = timeService.getTimeAfter(
            DefaultVals.getInt("buildings.farm.defaultBuildingTime"));
        upgradedBuilding.setFinishedAt(finishedAt);
        resourceService.addGenerationChange(kingdom, ResourceType.FOOD,
            DefaultVals.getInt("buildings.farm.food.perLevel"), finishedAt);
        break;
      case MINE:
        finishedAt = timeService.getTimeAfter(
            DefaultVals.getInt("buildings.mine.defaultBuildingTime"));
        upgradedBuilding.setFinishedAt(finishedAt);
        resourceService.addGenerationChange(kingdom, ResourceType.GOLD,
            DefaultVals.getInt("buildings.mine.gold.perLevel"), finishedAt);
        break;
      default:
        break;
    }
    buildingRepository.save(upgradedBuilding);
    return upgradedBuilding;
  }

  private void validateBuildingUpgrade(Long buildingId, Kingdom kingdom)
      throws BuildingIdNotBelongToKingdomException, InvalidBuildingIdException,
      InvalidBuildingTypeException, NotEnoughResourcesException,
      MaximumLevelReachedException, InvalidBuildingUpgradeException, DefaultValuesFileException,
      ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    var buildingType = getBuildingById(buildingId).getType();
    if (!isBuildingBelongToKingdom(buildingId, kingdom)) {
      throw new BuildingIdNotBelongToKingdomException(
          "Provided building ID not belong to the kingdom ID");
    }
    if (!buildingType.equals(BuildingType.TOWNHALL) && !hasTownhall(kingdom)) {
      throw new InvalidBuildingUpgradeException(
          "The kingdom has no townhall");
    }
    if (!isLevelLowerThanTownhall(buildingId, kingdom.getUser())) {
      throw new InvalidBuildingUpgradeException(
          "Level of the building is not lower then Townhall's level");
    }
    var goldAmount = resourceService.getGold(kingdom).getAmount();
    if (!checkIfEnoughGoldToCreateBuilding(goldAmount, buildingType)) {
      throw new NotEnoughResourcesException("Not enough resource");
    }
    if (!checkMaxLevel(buildingId)) {
      throw new MaximumLevelReachedException("Maximum level is 20");
    }
    if (checkIfBuildingsFinishedAndTownhallReady(buildingId, kingdom.getUser())) {
      throw new InvalidBuildingUpgradeException("Cannot upgrade at this time, try it later");
    }
  }

  private boolean checkIfBuildingsFinishedAndTownhallReady(Long buildingId, UserEntity userEntity)
      throws InvalidBuildingIdException {
    var upgradedBuilding = getBuildingById(buildingId);
    var finishedTime = upgradedBuilding.getFinishedAt();
    if (timeService.getTime() >= finishedTime) {
      return false;
    }
    return timeService.getTime() < getTownhalFromUserEntity(userEntity).getFinishedAt();
  }

  private boolean isLevelLowerThanTownhall(Long buildingId, UserEntity userEntity)
      throws InvalidBuildingIdException {
    var townhallLevel = getTownhalFromUserEntity(userEntity).getLevel();
    var building = getBuildingFromBuildingId(buildingId);
    if (!building.getType().equals(BuildingType.TOWNHALL)) {
      return building.getLevel() < townhallLevel;
    } else {
      return true;
    }
  }

  private Building getBuildingFromBuildingId(Long buildingId) throws InvalidBuildingIdException {
    return buildingRepository.findById(buildingId)
        .orElseThrow(() -> new InvalidBuildingIdException("Invalid building ID"));
  }

  private Building getTownhalFromUserEntity(UserEntity userEntity)
      throws InvalidBuildingIdException {
    var optionalTownhall = userEntity.getKingdom().getBuildings()
        .stream().filter(building -> building instanceof Townhall).findFirst();
    if (optionalTownhall.isEmpty()) {
      throw new InvalidBuildingIdException("Townhall missing");
    }
    return optionalTownhall.get();
  }

  private boolean checkMaxLevel(Long buildingId) throws InvalidBuildingIdException {
    return getBuildingFromBuildingId(buildingId).getLevel() < 20;
  }

  @Override
  public void remove(Building building) throws DefaultValuesFileException {
    var kingdom = building.getKingdom();
    var time = timeService.getTime();
    switch (building.getType()) {
      case TOWNHALL:
        resourceService.addGenerationChange(kingdom, ResourceType.FOOD,
            building.getLevel() * DefaultVals.getInt("buildings.townhall.food"), time);
        resourceService.addGenerationChange(kingdom, ResourceType.GOLD,
            building.getLevel() * DefaultVals.getInt("buildings.townhall.gold"), time);
        break;
      case FARM:
        resourceService.addGenerationChange(kingdom, ResourceType.FOOD,
            -DefaultVals.getInt("buildings.farm.food.base")
                - building.getLevel() * DefaultVals.getInt("buildings.farm.food.perLevel"), time);
        break;
      case MINE:
        resourceService.addGenerationChange(kingdom, ResourceType.GOLD,
            -DefaultVals.getInt("buildings.mine.gold.base")
                - building.getLevel() * DefaultVals.getInt("buildings.mine.gold.perLevel"), time);
        break;
      default:
        break;
    }
    buildingRepository.delete(building);
  }

  @Override
  public void removeByKingdom(Kingdom kingdom) {
    buildingRepository.deleteAllByKingdom(kingdom);
  }

  @Override
  public void update(Building building) {
    buildingRepository.save(building);
  }

  private BuildingType convertStringToBuildingType(String buildingType)
      throws InvalidBuildingTypeException {
    return Enums
        .getIfPresent(BuildingType.class, buildingType)
        .toJavaUtil()
        .orElseThrow(() -> new InvalidBuildingTypeException("Invalid building type"));
  }

  private boolean isBuildingBelongToKingdom(long buildingId, Kingdom kingdom) {
    return kingdom.getBuildings().stream().map(Building::getId).anyMatch(i -> i.equals(buildingId));
  }
}
