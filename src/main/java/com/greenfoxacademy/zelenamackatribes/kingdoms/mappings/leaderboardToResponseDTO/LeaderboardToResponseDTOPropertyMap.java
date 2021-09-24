package com.greenfoxacademy.zelenamackatribes.kingdoms.mappings.leaderboardToResponseDTO;

import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.LeaderboardPageDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.LeaderboardResponseDTO;
import org.modelmapper.PropertyMap;

public class LeaderboardToResponseDTOPropertyMap
    extends PropertyMap<LeaderboardPageDTO, LeaderboardResponseDTO> {

  private LeaderboardCurrentPageNumberConverter pageNumberConverter;
  private LeaderboardKingdomScoresConverter kingdomScoresConverter;

  public LeaderboardToResponseDTOPropertyMap() {
    super();
    this.pageNumberConverter = new LeaderboardCurrentPageNumberConverter();
    this.kingdomScoresConverter = new LeaderboardKingdomScoresConverter();
  }

  @Override
  protected void configure() {
    using(pageNumberConverter)
        .map(source.getKingdomScores().getNumber(), destination.getCurrentPageNumber());
    map(source.getKingdomScores().getTotalPages(), destination.getTotalPageNumber());
    map(source.getKingdomScores().getSize(), destination.getPageSize());
    using(kingdomScoresConverter)
        .map(source.getKingdomScores().getContent(), destination.getScores());
  }
}
