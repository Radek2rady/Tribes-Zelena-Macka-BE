package com.greenfoxacademy.zelenamackatribes.chat.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequestDTO {

  @NotBlank(message = "Message text is required.")
  @Size(max = 512, message = "Message too long, maximum 512 characters allowed.")
  private String message;
}
