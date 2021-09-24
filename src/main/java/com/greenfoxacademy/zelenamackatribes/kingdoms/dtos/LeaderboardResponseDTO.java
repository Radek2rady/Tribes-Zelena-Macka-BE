package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderboardResponseDTO {
  private int currentPageNumber;
  private int totalPageNumber;
  private int pageSize;
  private String scoreType;
  private List<KingdomScoreDTO> scores;
}
