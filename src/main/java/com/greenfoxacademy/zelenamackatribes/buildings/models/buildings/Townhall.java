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
public class Townhall extends Building {

  public Townhall() throws DefaultValuesFileException {
    this.setType(BuildingType.TOWNHALL);
    this.setHp(
        DefaultVals.getInt("buildings.townhall.defaultHp")
    );
    this.setFinishedAt(new TimeServiceImpl().getTimeAfter(
        DefaultVals.getInt("buildings.townhall.defaultBuildingTime")
    ));
  }
}
