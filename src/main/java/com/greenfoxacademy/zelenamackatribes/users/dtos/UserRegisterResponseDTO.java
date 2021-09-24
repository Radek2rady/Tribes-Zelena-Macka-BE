package com.greenfoxacademy.zelenamackatribes.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterResponseDTO {

  private Long id;
  private String username;
  private String email;
  private Long kingdomId;
  private String avatar;
  private Integer points;
}
