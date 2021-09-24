package com.greenfoxacademy.zelenamackatribes.buildings.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingDTO;
import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingListResponseDTO;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.BuildingIdNotBelongToKingdomException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class BuildingControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;

  @Autowired
  private JwtService jwtService;
  @Autowired
  private KingdomService kingdomService;
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private KingdomRepository kingdomRepository;
  @Autowired
  private BuildingRepository buildingRepository;
  @Autowired
  private ResourceRepository resourceRepository;

  private Kingdom testKingdom;
  private String token;

  @BeforeEach
  void setup() throws Exception {
    cleanerService.cleanDatabase();
    UserEntity user = new UserEntity();
    user.setUsername("fake user");
    user.setPassword("dummy pass");
    user = userRepository.save(user);
    testKingdom = kingdomService.createKingdom(user, "dummy kingdom");
    user.setKingdom(testKingdom);
    testKingdom.getResources().forEach(r -> r.setAmount(200));
    resourceRepository.saveAll(testKingdom.getResources());
    token = jwtService.getPrefix() + jwtService.generate(user);
  }

  @Test
  void builtNewBuildingShouldReturnNotEnoughResourceException() throws Exception {
    testKingdom.getResources().forEach(r -> r.setAmount(50));
    resourceRepository.saveAll(testKingdom.getResources());
    mockMvc.perform(post("/kingdom/buildings")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\": \"farm\"}"))
        .andExpect(status().isConflict())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Don't have enough gold\"\n"
            + "}"));
  }

  @Test
  void shouldReturnMissingParamException() throws Exception {
    mockMvc.perform(post("/kingdom/buildings")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Missing parameter(s): type!\"\n"
            + "}"));
  }

  @Test
  void shouldReturnStatusOk() throws Exception {
    mockMvc.perform(post("/kingdom/buildings")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\": \"academy\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.type").value("ACADEMY"))
        .andExpect(jsonPath("$.level").value(1))
        .andExpect(jsonPath("$.hp").value(150));
  }

  @Test
  void shouldReturnStatusNotAcceptableIfNoTownhallInKingdom() throws Exception {
    buildingRepository.deleteAll(testKingdom.getBuildings());
    mockMvc.perform(post("/kingdom/buildings")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\": \"academy\"}"))
        .andExpect(status().isNotAcceptable())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof InvalidBuildingTypeException))
        .andExpect(
            result -> assertEquals("The kingdom has no townhall",
                result.getResolvedException().getMessage()));
  }

  @Test
  public void shouldReturnListOfBuildings() throws Exception {
    mockMvc.perform(get("/kingdom/buildings")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(
            (new ObjectMapper())
                .valueToTree(
                    modelMapper.map(testKingdom.getBuildings(), BuildingListResponseDTO.class))
                .toString()));
  }

  @Test
  public void shouldReturnForbiddenActionOnWrongKingdom() throws Exception {
    var otherUser = new UserEntity();
    otherUser.setUsername("other user");
    otherUser = userRepository.save(otherUser);
    var otherKingdom = kingdomService.createKingdom(otherUser, "other kingdom");
    var buildingId = otherKingdom.getBuildings().stream().findFirst().get().getId();

    mockMvc.perform(get(String.format("/kingdom/buildings/%1$d", buildingId))
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ForbiddenActionException))
        .andExpect(
            result -> assertEquals("Forbidden action",
                result.getResolvedException().getMessage()));
  }

  @Test
  public void shouldReturnIdNotFoundOnWrongBuildingId() throws Exception {
    var maxBuildingId = buildingRepository.findAll().stream()
        .mapToLong(b -> b.getId()).max().orElse(1L);

    mockMvc.perform(get(String.format("/kingdom/buildings/%1$d", maxBuildingId + 1))
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof InvalidBuildingIdException))
        .andExpect(
            result -> assertEquals("Building id not found",
                result.getResolvedException().getMessage()));
  }

  @Test
  public void shouldReturnBuildingWhenAllOk() throws Exception {
    var building = testKingdom.getBuildings().stream().findFirst().get();

    mockMvc.perform(get(String.format("/kingdom/buildings/%1$d", building.getId()))
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().json(
            (new ObjectMapper())
                .valueToTree(modelMapper.map(building, BuildingDTO.class))
                .toString()));
  }

  @Test
  public void shouldReturnStatusNotAcceptableWhenCreatingSecondTownhall() throws Exception {
    mockMvc.perform(post("/kingdom/buildings")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"type\": \"townhall\"}"))
        .andExpect(status().isNotAcceptable())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof InvalidBuildingTypeException))
        .andExpect(
            result -> assertEquals("Kingdom already has a Townhall, cannot build another Townhall",
                result.getResolvedException().getMessage()));
  }

  @Test
  public void upgradeBuildingOK() throws Exception {
    var townhall = buildingRepository.findAllByKingdom(testKingdom).stream()
        .filter(building -> building.getType().equals(
            BuildingType.TOWNHALL)).findFirst().orElse(null);

    mockMvc.perform(put("/kingdom/buildings/" + townhall.getId())
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.type").value("TOWNHALL"))
        .andExpect(jsonPath("$.level").value(townhall.getLevel() + 1))
        .andExpect(jsonPath("$.hp").value(200));
  }

  @Test
  public void shouldReturnStatusIsNotFoundIdNotBelongToKingdom() throws Exception {
    Kingdom fakekingdom2 = new Kingdom();
    var townhall = buildingRepository.findAllByKingdom(testKingdom).stream()
        .filter(building1 -> building1.getType().equals(BuildingType.TOWNHALL)).findFirst()
        .orElse(null);
    kingdomRepository.save(fakekingdom2);
    townhall.setKingdom(fakekingdom2);
    buildingRepository.save(townhall);

    mockMvc.perform(put("/kingdom/buildings/" + townhall.getId())
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof BuildingIdNotBelongToKingdomException))
        .andExpect(
            result -> assertEquals("Provided building ID not belong to the kingdom ID",
                result.getResolvedException().getMessage()));
  }
}
