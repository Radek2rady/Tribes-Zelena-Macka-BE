package com.greenfoxacademy.zelenamackatribes.users.integration;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.users.controllers.UserController;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserRegisterRequestDTO;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.users.services.AvatarService;
import com.greenfoxacademy.zelenamackatribes.users.services.UserService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class UserControllerRegisterTest {

  private UserController userController;
  private UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AvatarService avatarService;

  @Autowired
  private JwtService jwtService;

  @BeforeEach
  public void setup() {
    userService = Mockito.mock(UserService.class);
    userController = new UserController(userService, modelMapper, avatarService, jwtService);
  }

  @Test
  public void registerNewUserOk() throws Exception {
    Kingdom kingdom = new Kingdom();
    UserEntity userEntity = UserEntity.builder()
        .username("James Bond")
        .password("password")
        .email("james@bond.us")
        .kingdom(kingdom)
        .build();

    UserRegisterRequestDTO userRegisterRequestDTO = new UserRegisterRequestDTO("James Bond",
        "password", "kingdom",
        "james@bond.us");

    Mockito.when(userService
        .createNewUser(userRegisterRequestDTO.getUsername(),
            userRegisterRequestDTO.getPassword(),
            userRegisterRequestDTO.getEmail(), userRegisterRequestDTO.getKingdomName()))
        .thenReturn(userEntity);
    ResponseEntity<?> response = userController.registerNewUser(userRegisterRequestDTO);
    assertEquals(HttpStatus.valueOf(201), response.getStatusCode());
  }

  @Test
  public void ifNoEmailShouldThrowException() throws Exception {
    UserRegisterRequestDTO userRegisterRequestDTO = new UserRegisterRequestDTO();
    userRegisterRequestDTO.setUsername("James Bond");
    userRegisterRequestDTO.setPassword("12345678");
    userRegisterRequestDTO.setKingdomName("new");

    String registerJson = new ObjectMapper().writeValueAsString(userRegisterRequestDTO);

    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Email is required.")));
  }

  @Test
  public void ifNoUsernameShouldThrowException() throws Exception {
    UserRegisterRequestDTO userRegisterRequestDTO = new UserRegisterRequestDTO();
    userRegisterRequestDTO.setPassword("12345678");
    userRegisterRequestDTO.setEmail("james@bond.cz");
    userRegisterRequestDTO.setKingdomName("new");

    String registerJson = new ObjectMapper().writeValueAsString(userRegisterRequestDTO);

    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is required.")));
  }

  @Test
  public void ifNoPasswordShouldThrowException() throws Exception {
    UserRegisterRequestDTO userRegisterRequestDTO = new UserRegisterRequestDTO();
    userRegisterRequestDTO.setUsername("James Bond");
    userRegisterRequestDTO.setEmail("james@bond.cz");
    userRegisterRequestDTO.setKingdomName("new");

    String registerJson = new ObjectMapper().writeValueAsString(userRegisterRequestDTO);

    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is required.")));
  }

  @Test
  public void ifNoKingdomNameShouldThrowException() throws Exception {
    UserRegisterRequestDTO userRegisterRequestDTO = new UserRegisterRequestDTO();
    userRegisterRequestDTO.setUsername("James Bond");
    userRegisterRequestDTO.setPassword("12345678");
    userRegisterRequestDTO.setEmail("james@bond.cz");

    String registerJson = new ObjectMapper().writeValueAsString(userRegisterRequestDTO);

    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON)
            .content(registerJson))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Kingdom name is required.")));
  }
}
