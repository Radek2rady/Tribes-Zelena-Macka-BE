package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import lombok.Data;

@Data
public class LeaderboardRequestDTO {

  private Integer pageNo;
  private Integer pageSize;
  private String scoreType;
}
