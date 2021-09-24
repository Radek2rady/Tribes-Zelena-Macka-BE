package com.greenfoxacademy.zelenamackatribes.utils.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingCreateRequestDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserLoginRequestDTO;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtServiceImpl;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
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
@SetEnvironmentVariable(
    key = "SECRET_KEY",
    value = "xQXH2IIkqT3TkUUhtwAeZjaJU17w3y/e8Ucgo78e2Cc="
)
public class JwtAuthorizationFilterTest {

  private String token;
  private UserEntity user;
  private Kingdom kingdom;
  private BuildingCreateRequestDTO building;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private CleanerService cleanerService;

  @Autowired
  private JwtService jwtService;
  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void init() throws Exception {
    cleanerService.cleanDatabase();
    user = new UserEntity();
    user.setUsername("Tester");
    user.setPassword(bCryptPasswordEncoder.encode("OK"));
    user.setIsEmailConfirmed(true);
    userRepository.save(user);
    kingdom = kingdomService.createKingdom(user, "fakeKingdom");
    user.setKingdom(kingdom);
    building = new BuildingCreateRequestDTO();
    building.setType("CCCP panelak");
    token = jwtService.generate(user);
  }

  @Test
  @Order(1)
  public void enterUnsecuredLoginEndpoint() throws Exception {
    UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
    userLoginRequestDTO.setUsername("Tester");
    userLoginRequestDTO.setPassword("OK");

    String jwtStringHeader = token.substring(0, token.indexOf("."));

    mockMvc
        .perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequestDTO))
        )
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.status", is("ok")))
        .andExpect(jsonPath("$.token", Matchers.startsWith(jwtStringHeader)));
  }

  @Test
  @Order(2)
  public void enterSecuredEndpointWithoutHeader() throws Exception {
    mockMvc
        .perform(
            post("/kingdom/buildings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Missing Authentication header.")));
  }

  @Test
  @Order(3)
  public void enterSecuredEndpointWithoutContentInHeader() throws Exception {
    mockMvc
        .perform(
            post("/kingdom/buildings")
                .header("Authorization", "")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Authentication header without content.")));
  }

  @Test
  @Order(4)
  public void enterSecuredEndpointWithoutTokenBearer() throws Exception {
    mockMvc
        .perform(
            post("/kingdom/buildings")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Missing JWT token bearer.")));
  }

  @Test
  @Order(5)
  public void enterSecuredEndpointWithoutToken() throws Exception {
    mockMvc
        .perform(
            post("/kingdom/buildings")
                .header("Authorization", jwtService.getPrefix())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Missing Authentication JWT Token.")));
  }

  @Test
  @Order(6)
  public void enterSecuredEndpointWithExpiredToken() throws Exception {
    TimeService timeServiceMock = Mockito.mock(TimeService.class);
    JwtService jwtServiceMock = new JwtServiceImpl(timeServiceMock);

    Mockito
        .when(timeServiceMock.getTime())
        .thenReturn(1L);
    String token = jwtServiceMock.generate(user);

    mockMvc
        .perform(
            post("/kingdom/buildings")
                .header("Authorization", jwtService.getPrefix() + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(401))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("JWT token has expired.")));
  }

  @Test
  @Order(7)
  public void enterSecuredEndpointWithInvalidToken() throws Exception {
    mockMvc
        .perform(
            post("/kingdom/buildings")
                .header("Authorization",
                    jwtService.getPrefix() + token.substring(0, token.length() - 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(401))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("JWT token signature is not valid.")));
  }

  @Test
  @Order(8)
  public void allowToEnterSecuredEndpoint() throws Exception {
    mockMvc
        .perform(
            post("/kingdom/buildings")
                .header("Authorization", jwtService.getPrefix() + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(building))
        )
        .andExpect(status().is(406))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Invalid building type")));
  }
}
