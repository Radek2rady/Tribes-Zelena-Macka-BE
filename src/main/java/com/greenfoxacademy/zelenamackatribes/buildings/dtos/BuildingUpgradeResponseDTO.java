package com.greenfoxacademy.zelenamackatribes.buildings.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildingUpgradeResponseDTO extends BuildingDTO {

  private long id;
  private String type;
  private int level;
  private int hp;
  private long startedAt;
  private long finishedAt;
}
