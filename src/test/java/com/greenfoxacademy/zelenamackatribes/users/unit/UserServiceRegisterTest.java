package com.greenfoxacademy.zelenamackatribes.users.unit;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserRegisterRequestDTO;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.users.services.UserServiceImpl;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationTokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class UserServiceRegisterTest {

  @Autowired
  private UserServiceImpl userService;

  @Autowired
  private UserRepository userRepository;
  @MockBean
  private ConfirmationTokenService confirmationTokenService;

  @Test
  @DisplayName("Register and save new user")
  public void createNewUserAccountOk() throws Exception {
    UserRegisterRequestDTO newUserDTO = new UserRegisterRequestDTO("Patrik Schick",
        "12345678",
        "patrik@schick.cz",
        "Schick kingdom");
    Kingdom kingdom = new Kingdom();
    kingdom.setKingdomName(newUserDTO.getKingdomName());
    UserEntity createdEntity = userService.createNewUser(newUserDTO.getUsername(),
        newUserDTO.getPassword(), newUserDTO.getEmail(), newUserDTO.getKingdomName());
    createdEntity.setIsEmailConfirmed(true);
    Mockito.when(confirmationTokenService.createConfirmationTokenAndSendMail(Mockito.any()))
        .thenReturn("b35f0747-c36e-40d3-bacd-d3bd176a5088");
    Assertions.assertEquals("Patrik Schick", createdEntity.getUsername());
    Assertions.assertEquals("patrik@schick.cz", createdEntity.getEmail());
    Assertions.assertEquals("Schick kingdom", createdEntity.getKingdom().getKingdomName());
  }

  @Test
  @DisplayName("Find user by name")
  public void findUserByNameOk() {
    UserEntity newUser = new UserEntity("new", "12345678");
    userRepository.save(newUser);
    UserEntity actualUser = userService.getByUsername("new");
    Assertions.assertEquals(newUser.getUsername(), actualUser.getUsername());
    Assertions.assertEquals(newUser.getPassword(), actualUser.getPassword());
  }
}
