package com.greenfoxacademy.zelenamackatribes.buildings.models.buildings;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeServiceImpl;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Farm extends Building {

  public Farm() throws DefaultValuesFileException {
    this.setType(BuildingType.FARM);
    this.setHp(
        DefaultVals.getInt("buildings.farm.defaultHp")
    );
    this.setFinishedAt(new TimeServiceImpl().getTimeAfter(
        DefaultVals.getInt("buildings.farm.defaultBuildingTime")
    ));
  }
}
