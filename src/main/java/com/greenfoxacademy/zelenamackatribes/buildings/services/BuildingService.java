package com.greenfoxacademy.zelenamackatribes.buildings.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.BuildingIdNotBelongToKingdomException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingUpgradeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MaximumLevelReachedException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import java.util.InputMismatchException;
import java.util.List;

public interface BuildingService {

  Building createBuilding(String buildingType, Kingdom kingdom)
      throws InvalidBuildingTypeException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException, InputMismatchException,
      DefaultValuesFileException;

  Building createBuilding(BuildingType buildingType, Kingdom kingdom)
      throws InvalidBuildingTypeException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException, InputMismatchException,
      DefaultValuesFileException;

  Building createBuilding(BuildingType buildingType, Kingdom kingdom, boolean starterBuilding)
      throws InvalidBuildingTypeException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException, InputMismatchException,
      DefaultValuesFileException;

  boolean checkIfEnoughGoldToCreateBuilding(int gold, BuildingType type)
      throws InvalidBuildingTypeException, DefaultValuesFileException;

  boolean hasTownhall(Kingdom kingdom);

  Building getBuildingById(Long buildingId) throws InvalidBuildingIdException;

  List<Building> getBuildingsForKingdom(Kingdom kingdom);

  Building getBuildingForKingdom(Long buildingId, Kingdom kingdom)
      throws InvalidBuildingIdException, ForbiddenActionException;

  Building upgradeBuilding(Kingdom kingdom, Long buildingId)
      throws MaximumLevelReachedException, NotEnoughResourcesException,
      InvalidBuildingIdException, InvalidBuildingUpgradeException,
      InvalidBuildingTypeException, BuildingIdNotBelongToKingdomException, KingdomNotFoundException,
      ResourceNotFoundException, DefaultValuesFileException,
      InvalidNumberOfResourceObjectsException;

  void remove(Building building) throws DefaultValuesFileException;

  void removeByKingdom(Kingdom kingdom);

  void update(Building building);
}
