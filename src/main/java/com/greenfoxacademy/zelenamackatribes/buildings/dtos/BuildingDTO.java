package com.greenfoxacademy.zelenamackatribes.buildings.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO {

  private long id;
  private String type;
  private int level;
  private int hp;
  private long startedAt;
  private long finishedAt;

}
