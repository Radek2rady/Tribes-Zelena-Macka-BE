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
public class Academy extends Building {

  public Academy() throws DefaultValuesFileException {
    this.setType(BuildingType.ACADEMY);
    this.setHp(
        DefaultVals.getInt("buildings.academy.defaultHp")
    );
    this.setFinishedAt(new TimeServiceImpl().getTimeAfter(
        DefaultVals.getInt("buildings.academy.defaultBuildingTime")
    ));
  }
}
