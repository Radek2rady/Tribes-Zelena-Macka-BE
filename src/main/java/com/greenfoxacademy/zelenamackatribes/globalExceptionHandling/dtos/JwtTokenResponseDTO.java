package com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokenResponseDTO {

  private long id;
  private String username;
  private long kingdomId;
  private String kingdomName;
}
