package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.KingdomScore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@AllArgsConstructor
public class LeaderboardPageDTO {
  private Page<KingdomScore> kingdomScores;
  private String scoreType;
}
