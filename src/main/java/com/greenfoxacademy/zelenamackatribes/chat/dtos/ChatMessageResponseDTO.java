package com.greenfoxacademy.zelenamackatribes.chat.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponseDTO {

  private String message;
  private String createdAt;
  private String userName;
}
