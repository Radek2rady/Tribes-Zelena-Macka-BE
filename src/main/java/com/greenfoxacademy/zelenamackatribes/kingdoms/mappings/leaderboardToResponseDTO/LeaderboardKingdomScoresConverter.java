package com.greenfoxacademy.zelenamackatribes.kingdoms.mappings.leaderboardToResponseDTO;

import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomScoreDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.KingdomScore;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

public class LeaderboardKingdomScoresConverter
    implements Converter<List<KingdomScore>, List<KingdomScoreDTO>> {

  private ModelMapper modelMapper;

  public LeaderboardKingdomScoresConverter() {
    super();
    this.modelMapper = new ModelMapper();
  }

  @Override
  public List<KingdomScoreDTO> convert(
      MappingContext<List<KingdomScore>, List<KingdomScoreDTO>> context) {
    return context.getSource().stream()
        .map(item -> modelMapper.map(item, KingdomScoreDTO.class))
        .collect(Collectors.toList());
  }
}
