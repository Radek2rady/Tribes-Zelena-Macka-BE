package com.greenfoxacademy.zelenamackatribes.resources.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceGenerationChangeRepository;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SetEnvironmentVariable(
    key = "SECRET_KEY",
    value = "xQXH2IIkqT3TkUUhtwAeZjaJU17w3y/e8Ucgo78e2Cc="
)
@TestMethodOrder(OrderAnnotation.class)
public class ResourcesControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private KingdomRepository kingdomRepository;
  @Autowired
  private ResourceRepository resourceRepository;
  @Autowired
  private ResourceGenerationChangeRepository resourceGenerationChangeRepository;
  @Autowired
  private KingdomService kingdomService;

  private String token;
  private UserEntity user;
  private Kingdom testKingdom;

  @BeforeEach
  void setup() throws Exception {
    cleanerService.cleanDatabase();
    user = new UserEntity();
    user.setUsername("fakeUser");
    user.setPassword("dummy pass");
    userRepository.save(user);
    testKingdom = kingdomService.createKingdom(user, "fakeKingdom");
    kingdomRepository.save(testKingdom);
    user.setKingdom(testKingdom);
    token = jwtService.getPrefix() + jwtService.generate(user);
  }

  @Test
  @Order(1)
  public void resourceControllerInvalidNumberOfResourceObjects() throws Exception {
    resourceGenerationChangeRepository.deleteAll();
    resourceRepository.deleteAll();
    mockMvc.perform(get("/kingdom/resources")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Unexpected number of resources found (wants=2, found=0)\"\n"
            + "}"));

    resourceRepository.save(new Resource(null, ResourceType.FOOD, 40, testKingdom, 30, 1L));

    mockMvc.perform(get("/kingdom/resources")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Unexpected number of resources found (wants=2, found=1)\"\n"
            + "}"));

    resourceRepository.saveAll(Arrays.asList(
        new Resource(null, ResourceType.FOOD, 42, testKingdom, 30, 1L),
        new Resource(null, ResourceType.FOOD, 44, testKingdom, 30, 1L)));

    mockMvc.perform(get("/kingdom/resources")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Unexpected number of resources found (wants=2, found=3)\"\n"
            + "}"));
  }

  @Test
  @Order(3)
  public void resourceControllerOk() throws Exception {
    resourceGenerationChangeRepository.deleteAll();
    resourceRepository.deleteAll();
    resourceRepository.saveAll(Arrays.asList(
        new Resource(null, ResourceType.GOLD, 50, testKingdom, 50, 2L),
        new Resource(null, ResourceType.FOOD, 40, testKingdom, 30, 1L)));

    MvcResult result = mockMvc.perform(get("/kingdom/resources")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json("{\n\"resources\":["
            + "{\"type\":\"GOLD\",\"amount\":50,\"generation\":50,\"updatedAt\":2},"
            + "{\"type\":\"FOOD\",\"amount\":40,\"generation\":30,\"updatedAt\":1}]}"))
        .andReturn();
  }
}
