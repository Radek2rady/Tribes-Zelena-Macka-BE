package com.greenfoxacademy.zelenamackatribes.kingdoms.mappings.leaderboardToResponseDTO;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class LeaderboardCurrentPageNumberConverter implements Converter<Integer, Integer> {

  @Override
  public Integer convert(MappingContext<Integer, Integer> context) {
    return context.getSource() + 1;
  }
}
