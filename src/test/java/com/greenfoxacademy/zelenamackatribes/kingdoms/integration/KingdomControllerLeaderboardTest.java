package com.greenfoxacademy.zelenamackatribes.kingdoms.integration;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.repositories.TroopRepository;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationTokenService;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
class KingdomControllerLeaderboardTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BuildingService buildingService;
  @Autowired
  private TroopRepository troopRepository;
  @Autowired
  private KingdomRepository kingdomRepository;
  @Autowired
  private BuildingRepository buildingRepository;
  @Autowired
  private KingdomService kingdomService;
  @Autowired
  private LeaderboardService leaderboardService;
  @Autowired
  private ResourceService resourceService;
  @MockBean
  private ConfirmationTokenService confirmationTokenService;
  @Autowired
  private TroopService troopService;

  private UserEntity user;
  private UserEntity enemyUser;

  @BeforeEach
  void setup() throws Exception {
    Thread.sleep(100L);
    cleanerService.cleanDatabase();
    user = new UserEntity();
    user.setUsername("fakeUser");
    user.setPassword("dummy pass");
    user = userRepository.save(user);
    user.setKingdom(kingdomService.createKingdom(user, "Amandica"));
    enemyUser = new UserEntity();
    enemyUser.setUsername("enemyUser");
    enemyUser.setPassword("dummy pass");
    enemyUser = userRepository.save(enemyUser);
    enemyUser.setKingdom(kingdomService.createKingdom(enemyUser, "enemyKingdom"));
    List<Troop> userTroops = IntStream
        .range(0, 7)
        .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, user.getKingdom()))
        .collect(Collectors.toList());
    troopRepository.saveAll(userTroops);
    List<Troop> enemyTroops = IntStream
        .range(0, 2)
        .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, enemyUser.getKingdom()))
        .collect(Collectors.toList());
    troopRepository.saveAll(enemyTroops);
    buildingService.createBuilding(BuildingType.ACADEMY, user.getKingdom(), true);
    leaderboardService.recountKingdomScores();
  }

  @Test
  @Order(1)
  public void leaderboardPositiveOutcome() throws Exception {
    leaderboardService.recountKingdomScores();
    String url1 = "/kingdom/leaderboard?pageNo=1&pageSize=1&scoreType=totalScore&isHistory=false";
    mockMvc.perform(get(url1))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPageNumber", is(1)))
        .andExpect(jsonPath("$.totalPageNumber", is(2)))
        .andExpect(jsonPath("$.pageSize", is(1)))
        .andExpect(jsonPath("$.scoreType", is("totalScore")))
        .andExpect(jsonPath("$.scores[0].kingdomName", is("Amandica")))
        .andExpect(jsonPath("$.scores[0].totalScore", is(52)))
        .andExpect(jsonPath("$.scores[0].troopsScore", is(7)))
        .andExpect(jsonPath("$.scores[0].buildingsScore", is(5)))
        .andExpect(jsonPath("$.scores[0].resourcesScore", is(40)));
  }

  @Test
  @Order(2)
  public void leaderboardHistoryPositiveOutcome() throws Exception {
    leaderboardService.recountKingdomScores();
    Kingdom myKingdom = kingdomRepository.findKingdomByKingdomName("Amandica").orElse(null);
    Building myAcademy = buildingRepository.findAllByKingdom(myKingdom).stream()
        .filter(building -> building.getType().equals(BuildingType.ACADEMY))
        .findFirst().orElse(null);
    List<Troop> allTroops = troopRepository.findAll();
    List<Troop> myTroops = allTroops.stream()
        .filter(troop -> troop.getKingdom().getId().equals(myKingdom.getId()))
        .collect(Collectors.toList());
    troopRepository.deleteAll(myTroops);
    buildingRepository.delete(myAcademy);

    String url = "/kingdom/leaderboard?pageNo=1&pageSize=3&scoreType=troopsScore&isHistory=true";
    mockMvc.perform(get(url))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.currentPageNumber", is(1)))
        .andExpect(jsonPath("$.totalPageNumber", is(1)))
        .andExpect(jsonPath("$.pageSize", is(3)))
        .andExpect(jsonPath("$.scoreType", is("troopsScore")))
        .andExpect(jsonPath("$.scores[0].kingdomName", is("Amandica")))
        .andExpect(jsonPath("$.scores[0].totalScore", is(52)))
        .andExpect(jsonPath("$.scores[0].troopsScore", is(7)))
        .andExpect(jsonPath("$.scores[0].buildingsScore", is(5)))
        .andExpect(jsonPath("$.scores[0].resourcesScore", is(40)))
        .andExpect(jsonPath("$.scores[1].kingdomName", is("enemyKingdom")))
        .andExpect(jsonPath("$.scores[1].totalScore", is(46)))
        .andExpect(jsonPath("$.scores[1].troopsScore", is(2)))
        .andExpect(jsonPath("$.scores[1].buildingsScore", is(4)))
        .andExpect(jsonPath("$.scores[1].resourcesScore", is(40)));
  }

  @Test
  @Order(3)
  public void leaderboardPageNoTooHigh() throws Exception {
    String url =
        "/kingdom/leaderboard?pageNo=5&pageSize=3&scoreType=buildingsScore&isHistory=false";
    mockMvc.perform(get(url))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Not enough kingdoms to display given pageNo\"\n"
            + "}"));
  }

  @Test
  @Order(4)
  public void leaderboardPageNoTooLow() throws Exception {
    String url =
        "/kingdom/leaderboard?pageNo=0&pageSize=3&scoreType=buildingsScore&isHistory=false";
    mockMvc.perform(get(url))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Parameter pageNo must be a positive integer\"\n"
            + "}"));
  }

  @Test
  @Order(5)
  public void leaderboardPageSizeTooLow() throws Exception {
    String url = "/kingdom/leaderboard?pageNo=1&pageSize=-2&scoreType=troopsScore&isHistory=true";
    mockMvc.perform(get(url))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Parameter pageSize must be a positive integer\"\n"
            + "}"));
  }

  @Test
  @Order(6)
  public void leaderboardIncorrectScoreType() throws Exception {
    String url = "/kingdom/leaderboard?pageNo=1&pageSize=10&scoreType=buildsScore&isHistory=true";
    mockMvc.perform(get(url))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Parameter scoreType is not of a given type\"\n"
            + "}"));
  }
}
