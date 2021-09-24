package com.greenfoxacademy.zelenamackatribes.resources.integration;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SetEnvironmentVariable(
    key = "SECRET_KEY",
    value = "xQXH2IIkqT3TkUUhtwAeZjaJU17w3y/e8Ucgo78e2Cc="
)
public class ResourceGenerationChangeTest {

  public static TimeService timeService = Mockito.mock(TimeService.class);

  @TestConfiguration
  static class Config {

    @Bean
    @Primary
    public TimeService timeServiceMock() {
      return ResourceGenerationChangeTest.timeService;
    }
  }

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private KingdomService kingdomService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private KingdomRepository kingdomRepository;
  @Autowired
  private ResourceRepository resourceRepository;

  private String token;
  private UserEntity user;
  private Kingdom testKingdom;

  @BeforeEach
  void setup() throws Exception {
    Mockito.when(timeService.getTime()).thenReturn(0L);
    Mockito.when(timeService.getTimeAfter(Mockito.anyInt())).thenReturn(0L);
    cleanerService.cleanDatabase();
    user = new UserEntity();
    user.setUsername("fakeUser");
    user.setPassword("dummy pass");
    userRepository.save(user);
    testKingdom = kingdomService.createKingdom(user, "fakeKingdom");
    kingdomRepository.save(testKingdom);
    user.setKingdom(testKingdom);
    testKingdom.getResources().forEach(r -> r.setAmount(1000));
    resourceRepository.saveAll(testKingdom.getResources());
    Mockito.when(timeService.getTimeAfter(Mockito.anyInt())).thenReturn(2100000000L);
    token = jwtService.getPrefix() + jwtService.generate(user);
  }

  @Test
  public void resourceGenerationChangedWithNewBuilding() throws Exception {
    Mockito.when(timeService.getTimeAfter(Mockito.anyInt())).thenReturn(0L);
    mockMvc.perform(post("/kingdom/buildings")
        .header("Authorization", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"type\": \"farm\"}"));
    Mockito.when(timeService.getTime()).thenReturn(2000000000L);
    mockMvc.perform(get("/kingdom/resources")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resources[0].type", is("GOLD")))
        .andExpect(jsonPath("$.resources[0].amount", is(900)))
        .andExpect(jsonPath("$.resources[0].generation", is(20)))
        .andExpect(jsonPath("$.resources[0].updatedAt", is(0)))
        .andExpect(jsonPath("$.resources[1].type", is("FOOD")))
        .andExpect(jsonPath("$.resources[1].amount", is(1000)))
        .andExpect(jsonPath("$.resources[1].generation", is(30)))
        .andExpect(jsonPath("$.resources[1].updatedAt", is(2000000000)));
  }
}