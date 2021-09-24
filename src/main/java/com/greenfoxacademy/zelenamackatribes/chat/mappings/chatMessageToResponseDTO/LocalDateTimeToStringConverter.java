package com.greenfoxacademy.zelenamackatribes.chat.mappings.chatMessageToResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {

  @Override
  public String convert(MappingContext<LocalDateTime, String> context) {
    return context.getSource().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }
}
