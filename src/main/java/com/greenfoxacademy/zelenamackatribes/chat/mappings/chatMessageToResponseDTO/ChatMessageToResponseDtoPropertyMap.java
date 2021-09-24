package com.greenfoxacademy.zelenamackatribes.chat.mappings.chatMessageToResponseDTO;

import com.greenfoxacademy.zelenamackatribes.chat.dtos.ChatMessageResponseDTO;
import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import org.modelmapper.PropertyMap;

public class ChatMessageToResponseDtoPropertyMap extends
    PropertyMap<ChatMessage, ChatMessageResponseDTO> {

  private LocalDateTimeToStringConverter timeConverter;

  public ChatMessageToResponseDtoPropertyMap() {
    super();
    timeConverter = new LocalDateTimeToStringConverter();
  }

  @Override
  protected void configure() {
    map().setMessage(source.getMessage());
    map().setUserName(source.getUser().getUsername());
    using(timeConverter).map(source.getCreatedAt(), destination.getCreatedAt());
  }
}
