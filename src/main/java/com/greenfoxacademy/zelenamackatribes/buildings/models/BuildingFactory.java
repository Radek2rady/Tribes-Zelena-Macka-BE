package com.greenfoxacademy.zelenamackatribes.buildings.models;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Academy;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Farm;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Mine;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Townhall;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;

public class BuildingFactory {

  private BuildingFactory() {
  }

  public static Building createBuilding(BuildingType type)
      throws InvalidBuildingTypeException, DefaultValuesFileException {
    switch (type) {
      case ACADEMY:
        return new Academy();
      case FARM:
        return new Farm();
      case MINE:
        return new Mine();
      case TOWNHALL:
        return new Townhall();
      default:
        throw new InvalidBuildingTypeException("Invalid building type");
    }
  }

  public static int getPrice(BuildingType type)
      throws InvalidBuildingTypeException, DefaultValuesFileException {
    switch (type) {
      case ACADEMY:
        return DefaultVals.getInt("buildings.academy.price");
      case FARM:
        return DefaultVals.getInt("buildings.farm.price");
      case MINE:
        return DefaultVals.getInt("buildings.mine.price");
      case TOWNHALL:
        return DefaultVals.getInt("buildings.townhall.price");
      default:
        throw new InvalidBuildingTypeException("Invalid building type");
    }
  }
}
