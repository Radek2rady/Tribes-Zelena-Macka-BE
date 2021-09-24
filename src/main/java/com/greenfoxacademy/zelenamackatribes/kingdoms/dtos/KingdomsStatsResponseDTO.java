package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KingdomsStatsResponseDTO {

  private String result;

  @JsonInclude(Include.NON_NULL)
  private KingdomStatsDTO playerStatistics;

  @JsonInclude(Include.NON_NULL)
  private KingdomStatsDTO opponentStatistics;
}
