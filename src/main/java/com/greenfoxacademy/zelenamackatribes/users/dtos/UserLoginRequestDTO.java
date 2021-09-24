package com.greenfoxacademy.zelenamackatribes.users.dtos;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequestDTO {

  @NotBlank(message = "Username is required.")
  private String username;

  @NotBlank(message = "Password is required.")
  private String password;
}
