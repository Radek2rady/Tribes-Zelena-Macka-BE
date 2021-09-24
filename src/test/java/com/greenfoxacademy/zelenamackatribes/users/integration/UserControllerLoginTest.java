package com.greenfoxacademy.zelenamackatribes.users.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserLoginRequestDTO;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerLoginTest {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private KingdomService kingdomService;

  @BeforeEach
  public void setup() {
    cleanerService.cleanDatabase();
  }

  @Test
  @Order(1)
  public void doNotAllowGetRequest() throws Exception {
    mockMvc
        .perform(get("/login"))
        .andExpect(status().is(405));
  }

  @Test
  @Order(2)
  public void emptyRequestShouldThrowException() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("");

    mockMvc
        .perform(post("/login"))
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Request body malformed or missing.")));
  }

  @Test
  @Order(3)
  public void malformedRequestShouldThrowException() throws Exception {
    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{kjhmbn}")
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")));
  }

  @Test
  @Order(4)
  public void emptyUsernameAndPasswordShouldThrowMethodArgNotValid() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("");
    userLoginRequestDTO.setPassword("");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username and password are required.")));
  }

  @Test
  @Order(5)
  public void emptyUsernameShouldThrowMethodArgNotValid() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("");
    userLoginRequestDTO.setPassword("tiriri");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username is required.")));
  }

  @Test
  @Order(6)
  public void emptyPasswordShouldThrowMethodArgNotValid() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("No Passworder");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Password is required.")));
  }

  @Test
  @Order(7)
  public void userNotFoundShouldThrowInvalidUserCredentials() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("Noexistus");
    userLoginRequestDTO.setPassword("0000");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(401))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username or password is incorrect.")));
  }

  @Test
  @Order(8)
  public void ifWrongPasswordShouldThrowInvalidUserCredentials() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("Dumbo Forgeto");
    userLoginRequestDTO.setPassword("iForgetIt");

    UserEntity user = new UserEntity();
    user.setUsername("Dumbo Forgeto");
    user.setPassword("0000");
    user.setIsEmailConfirmed(true);
    userRepository.save(user);
    kingdomService.createKingdom(user, "dummy kingdom");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(401))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Username or password is incorrect.")));
  }

  @Test
  @Order(9)
  public void loginOK() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("Tester");
    userLoginRequestDTO.setPassword("12345678");

    UserEntity user = new UserEntity();
    user.setUsername("Tester");
    user.setPassword(bCryptPasswordEncoder.encode("12345678"));
    user.setIsEmailConfirmed(true);
    userRepository.save(user);
    kingdomService.createKingdom(user, "dummy kingdom2");

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.status", is("ok")));
  }
}
