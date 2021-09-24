package com.greenfoxacademy.zelenamackatribes.users.dtos;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginResponseDTO extends ResponseDTO {

  private String token;

  public UserLoginResponseDTO(String token) {
    status = "ok";
    this.token = token;
  }
}
