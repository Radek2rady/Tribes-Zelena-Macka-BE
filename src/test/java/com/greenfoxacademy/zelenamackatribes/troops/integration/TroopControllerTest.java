package com.greenfoxacademy.zelenamackatribes.troops.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.TroopNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.repositories.TroopRepository;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.users.services.UserService;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
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
public class TroopControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private TimeService timeService;
  @Autowired
  private UserService userService;
  @Autowired
  private KingdomService kingdomService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BuildingRepository buildingRepository;
  @Autowired
  private TroopRepository troopRepository;
  @Autowired
  private ResourceRepository resourceRepository;

  private String token;
  private UserEntity user;
  private UserEntity otherUser;
  private Kingdom testKingdom;
  private Building testAcademy;
  private Long testAcademyId;

  @BeforeEach
  void setup() throws Exception {
    cleanerService.cleanDatabase();
    user = new UserEntity();
    user.setUsername("fakeUser");
    user.setPassword("dummy pass");
    user = userRepository.save(user);
    testKingdom = kingdomService.createKingdom(user, "fakeKingdom");
    user.setKingdom(testKingdom);
    otherUser = new UserEntity();
    otherUser.setUsername("otherUser");
    otherUser.setPassword("dummy pass");
    otherUser = userRepository.save(otherUser);
    otherUser.setKingdom(kingdomService.createKingdom(otherUser, "otherKingdom"));
    testAcademy = testKingdom.getBuildings().stream()
        .filter(b -> b.getType() == BuildingType.ACADEMY).findFirst().get();
    testAcademyId = testAcademy.getId();
    token = jwtService.getPrefix() + jwtService.generate(user);
  }

  @Test
  public void troopControllerInvalidBuildingId() throws Exception {
    buildingRepository.deleteAll();

    mockMvc.perform(post("/kingdom/troops")
            .header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"buildingId\": \"1\"}"))
        .andExpect(status().isNotAcceptable())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Building id not found\"\n"
            + "}"));
  }

  @Test
  public void troopControllerWrongBuildingOwner() throws Exception {
    var buildingId = otherUser.getKingdom().getBuildings().stream()
        .filter(b -> b.getType() == BuildingType.ACADEMY).findFirst().get().getId();

    mockMvc.perform(post("/kingdom/troops")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%1d\"}", buildingId)))
        .andExpect(status().isForbidden())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Forbidden action\"\n"
            + "}"));
  }

  @Test
  void getTroopsShouldReturnListOfTroopsOk() throws Exception {
    var troops = Arrays.asList(
        new Troop(1, 150, 23, 11, 11531351L, 11531358L, testKingdom),
        new Troop(2, 110, 55, 21, 11544351L, 18831358L, testKingdom));
    troops = troopRepository.saveAll(troops);

    mockMvc.perform(get("/kingdom/troops")
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(content().json(String.format("{\n"
            + "    \"troops\": [\n"
            + "        {\n"
            + "            \"id\": %d,\n"
            + "            \"level\": 1,\n"
            + "            \"hp\": 150,\n"
            + "            \"attack\": 23,\n"
            + "            \"defence\": 11,\n"
            + "            \"startedAt\": 11531351,\n"
            + "            \"finishedAt\": 11531358\n"
            + "        },\n"
            + "        {\n"
            + "            \"id\": %d,\n"
            + "            \"level\": 2,\n"
            + "            \"hp\": 110,\n"
            + "            \"attack\": 55,\n"
            + "            \"defence\": 21,\n"
            + "            \"startedAt\": 11544351,\n"
            + "            \"finishedAt\": 18831358\n"
            + "        }\n"
            + "    ]\n"
            + "}", troops.get(0).getId(), troops.get(1).getId())));
  }

  @Test
  public void troopControllerInvalidAcademyId() throws Exception {

    var buildingId = testKingdom.getBuildings().stream()
        .filter(b -> b.getType() == BuildingType.FARM).findFirst().get().getId();

    mockMvc.perform(post("/kingdom/troops")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%1d\"}", buildingId)))
        .andExpect(status().isNotAcceptable())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Not a valid academy id\"\n"
            + "}"));
  }

  @Test
  void getTroopsShouldReturnEmptyList() throws Exception {

    mockMvc.perform(get("/kingdom/troops")
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().json("{\n"
            + "    \"troops\": []\n"
            + "}"));
  }

  @Test
  public void troopControllerNotEnoughResources() throws Exception {
    testKingdom.getResources().forEach(r -> r.setAmount(20));
    resourceRepository.saveAll(testKingdom.getResources());

    mockMvc.perform(post("/kingdom/troops")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isConflict())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Don't have enough gold\"\n"
            + "}"));
  }

  @Test
  public void troopCreatedOk() throws Exception {
    mockMvc.perform(post("/kingdom/troops")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.level").value(1))
        .andExpect(jsonPath("$.hp").value(20));
  }

  @Test
  void getTroopsShouldReturnInvalidTokenException() throws Exception {
    mockMvc.perform(get("/kingdom/troops")
            .header("Authorization", "Bearer fake.Val.ue"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTroopsWithEmptyHeaderShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/kingdom/troops"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTroopByIdWithEmptyHeaderShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/kingdom/troops/1"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTroopByIdShouldReturnTroopObject() throws Exception {
    long startedAt = timeService.getTime();
    long createdAt = startedAt + 50;
    Troop troop = new Troop(1, 100, 40, 20, startedAt, createdAt, testKingdom);
    troop = troopRepository.save(troop);

    mockMvc.perform(get(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token))
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.level").value(1))
        .andExpect(jsonPath("$.hp").value(100))
        .andExpect(jsonPath("$.attack").value(40))
        .andExpect(jsonPath("$.defence").value(20))
        .andExpect(jsonPath("$.startedAt").value(startedAt))
        .andExpect(jsonPath("$.finishedAt").value(createdAt));
  }

  @Test
  void getTroopByIdWithNoExistingIdShouldReturnTroopNotFoundException() throws Exception {
    long startedAt = timeService.getTime();
    long createdAt = startedAt + 20L;
    Troop troop = new Troop(1L, 1, 100, 40, 20, startedAt, createdAt, testKingdom, null);
    troopRepository.save(troop);

    mockMvc.perform(get("/kingdom/troops/42")
            .header("Authorization", token))
        .andExpect(status().is(404))
        .andExpect(result -> Assertions
            .assertTrue(result.getResolvedException() instanceof TroopNotFoundException));
  }

  @Test
  void getTroopByIdWhenTroopIdDoesNotBelongToUserReturnsForbiddenActionException()
      throws Exception {
    long startedAt = timeService.getTime();
    long createdAt = startedAt + 20;
    Troop troop = new Troop(1, 100, 40, 20, startedAt, createdAt, otherUser.getKingdom());
    troopRepository.save(troop);

    mockMvc.perform(get(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token))
        .andExpect(status().is(403))
        .andExpect(result -> Assertions
            .assertTrue(result.getResolvedException() instanceof ForbiddenActionException));
  }

  @Test
  void upgradeTroopWithMissingTroop() throws Exception {
    mockMvc.perform(put("/kingdom/troops/1")
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Troop id not found\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithInvalidBody() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token))
        .andExpect(status().isBadRequest());
  }

  @Test
  void upgradeTroopWithUnownedTroopError() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, otherUser.getKingdom());
    troop = troopRepository.save(troop);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isForbidden())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Forbidden action\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithUnownedAcademyError() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    testAcademy.setKingdom(otherUser.getKingdom());
    buildingRepository.save(testAcademy);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isForbidden())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Forbidden action\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithWrongBuildingTypeError() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    var buildingId = testKingdom.getBuildings().stream()
        .filter(b -> b.getType() == BuildingType.FARM).findFirst().get().getId();
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", buildingId)))
        .andExpect(status().isNotAcceptable())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Invalid academy id\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithUnfinishedAcademyError() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    testAcademy.setFinishedAt(timeService.getTimeAfter(1000));
    buildingRepository.save(testAcademy);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isNotAcceptable())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Cannot use unfinished academy for upgrade\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithTroopLevelOverAcademyError() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isBadRequest())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Cannot upgrade troop above academy level\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithFullAcademyError() throws Exception {
    var troops = Arrays.asList(
        new Troop(null, 1, 20, 10, 5, 0, 2000000000, testKingdom, testAcademy),
        new Troop(null, 1, 20, 10, 5, 0, 2000000000, testKingdom, testAcademy),
        new Troop(null, 1, 20, 10, 5, 0, 2000000000, testKingdom, testAcademy),
        new Troop(null, 1, 20, 10, 5, 0, 2000000000, testKingdom, testAcademy),
        new Troop(null, 1, 20, 10, 5, 0, 2000000000, testKingdom, testAcademy));
    troopRepository.saveAll(troops);
    testAcademy.setLevel(5);
    buildingRepository.save(testAcademy);
    mockMvc.perform(
            put(String.format("/kingdom/troops/%d", troopRepository.findAll().get(0).getId()))
                .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isConflict())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Not enough space in academy\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopWithoutResourcesError() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    testAcademy.setLevel(5);
    buildingRepository.save(testAcademy);
    var gold = testKingdom.getResources().stream()
        .filter(r -> r.getType().equals(ResourceType.GOLD)).findFirst().get();
    gold.setAmount(0);
    resourceRepository.save(gold);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().isConflict())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Don't have enough gold\"\n"
            + "}"));
  }

  @Test
  void upgradeTroopOk() throws Exception {
    var troop = new Troop(1, 20, 10, 5, 0, 0, testKingdom);
    troop = troopRepository.save(troop);
    testAcademy.setLevel(5);
    buildingRepository.save(testAcademy);
    var gold = testKingdom.getResources().stream()
        .filter(r -> r.getType().equals(ResourceType.GOLD)).findFirst().get();
    gold.setAmount(100);
    resourceRepository.save(gold);
    var startTime = timeService.getTime();
    var endTime = timeService.getTimeAfter(120);
    mockMvc.perform(put(String.format("/kingdom/troops/%d", troop.getId()))
            .header("Authorization", token).contentType(MediaType.APPLICATION_JSON)
            .content(String.format("{\"buildingId\": \"%d\"}", testAcademyId)))
        .andExpect(status().is(200))
        .andExpect(jsonPath("$.level").value(5))
        .andExpect(jsonPath("$.hp").value(100))
        .andExpect(jsonPath("$.attack").value(50))
        .andExpect(jsonPath("$.defence").value(25))
        .andExpect(jsonPath("$.startedAt").value(
            Matchers.oneOf((int) (startTime + 1), (int) startTime, (int) (startTime - 1),
                (int) (startTime - 2))))
        .andExpect(jsonPath("$.finishedAt").value(
            Matchers.oneOf((int) (endTime + 1), (int) endTime, (int) (endTime - 1),
                (int) (endTime - 2))));
  }
}
