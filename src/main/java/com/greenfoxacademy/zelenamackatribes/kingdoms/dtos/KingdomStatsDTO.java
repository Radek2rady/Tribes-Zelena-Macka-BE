package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KingdomStatsDTO {

  private Integer lostBuildings;
  private Integer lostTroops;

  @JsonInclude(Include.NON_NULL)
  private Integer lostGold;

  @JsonInclude(Include.NON_NULL)
  private Integer lostFood;

  @JsonInclude(Include.NON_NULL)
  private Integer earnedGold;

  @JsonInclude(Include.NON_NULL)
  private Integer earnedFood;

  public KingdomStatsDTO(Integer lostBuildings, Integer lostTroops, Integer lostGold,
      Integer lostFood, Integer earnedGold, Integer earnedFood) {
    this.lostBuildings = lostBuildings;
    this.lostTroops = lostTroops;
    this.lostGold = lostGold;
    this.lostFood = lostFood;
    this.earnedGold = earnedGold;
    this.earnedFood = earnedFood;
  }

  public KingdomStatsDTO(Integer lostBuildings, Integer lostTroops) {
    this.lostBuildings = lostBuildings;
    this.lostTroops = lostTroops;
  }
}
