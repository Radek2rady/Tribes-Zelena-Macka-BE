package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
public class KingdomScoreDTO {

  private String kingdomName;

  @JsonInclude(Include.NON_NULL)
  private Integer totalScore;

  @JsonInclude(Include.NON_NULL)
  private Integer buildingsScore;

  @JsonInclude(Include.NON_NULL)
  private Integer troopsScore;

  @JsonInclude(Include.NON_NULL)
  private Integer resourcesScore;

}
