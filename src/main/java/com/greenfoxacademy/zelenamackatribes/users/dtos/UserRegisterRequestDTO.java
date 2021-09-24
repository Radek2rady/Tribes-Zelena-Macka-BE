package com.greenfoxacademy.zelenamackatribes.users.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequestDTO {

  @NotBlank(message = "Username is required.")
  private String username;

  @NotBlank(message = "Password is required.")
  @Size(min = 8, message = "Password must have 8 characters.")
  private String password;

  @NotBlank(message = "Email is required.")
  @Email(message = "Email is not a valid address.")
  private String email;

  @NotBlank(message = "Kingdom name is required.")
  private String kingdomName;
}
