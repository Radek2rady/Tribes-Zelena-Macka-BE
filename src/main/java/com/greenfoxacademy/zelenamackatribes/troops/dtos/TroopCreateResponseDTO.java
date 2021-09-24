package com.greenfoxacademy.zelenamackatribes.troops.dtos;

import lombok.Data;

@Data
public class TroopCreateResponseDTO {

  private long id;
  private int level;
  private int hp;
  private int attack;
  private int defence;
  private long startedAt;
  private long finishedAt;

}
