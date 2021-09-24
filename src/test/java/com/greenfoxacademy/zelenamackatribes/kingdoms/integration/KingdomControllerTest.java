package com.greenfoxacademy.zelenamackatribes.kingdoms.integration;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Townhall;
import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomOutOfRangeException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.repositories.TroopRepository;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
public class KingdomControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BuildingRepository buildingRepository;
  @Autowired
  private BuildingService buildingService;
  @Autowired
  private ResourceService resourceService;
  @Autowired
  private TroopRepository troopRepository;
  @Autowired
  private KingdomService kingdomService;

  private String token;
  private UserEntity user;
  private UserEntity enemyUser;
  private List<Troop> userTroops;
  private List<Troop> enemyTroops;

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
    token = jwtService.getPrefix() + jwtService.generate(user);
  }

  @Test
  public void getKingdomByIdNotFound() throws Exception {
    mockMvc.perform(get("/kingdom/150")
            .header("Authorization", token))
        .andExpect(status().isNotFound())
        .andExpect(content().json("{\n"
            + "    \"status\": \"error\",\n"
            + "    \"message\": \"Kingdom id not found\"\n"
            + "}"));
  }

  @Test
  public void getKingdomByIdShouldReturnOk() throws Exception {
    mockMvc.perform(get(String.format("/kingdom/%d", user.getKingdom().getId()))
            .header("Authorization", token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getKingdom().getId()))
        .andExpect(jsonPath("$.name").value("Amandica"));
  }

  @Test
  public void getKingdomShouldReturnInvalidTokenException() throws Exception {
    mockMvc.perform(get("/kingdom/15")
            .header("Authorization", "fakeValue"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void initiateFightWithItselfShouldThrowForbiddenActionException() throws Exception {
    mockMvc.perform(post("/kingdom/fight/" + user.getKingdom().getId())
            .header("Authorization", token))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ForbiddenActionException))
        .andExpect(
            result -> assertEquals("Kingdom is not able to fight with itself",
                result.getResolvedException().getMessage()));
  }

  @Test
  public void fightWithNoTroopsShouldThrowForbiddenActionException() throws Exception {
    mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
            .header("Authorization", token))
        .andExpect(status().isForbidden())
        .andExpect(result -> assertTrue(
            result.getResolvedException() instanceof ForbiddenActionException))
        .andExpect(
            result -> assertEquals("A kingdom without troops can't initiate a battle",
                result.getResolvedException().getMessage()));
  }

  @Nested
  class KingdomControllerTestWithTroops {

    @BeforeEach
    void creatingListOfTroopsSetup() {
      userTroops = IntStream
          .range(0, 5)
          .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, user.getKingdom()))
          .collect(Collectors.toList());
      troopRepository.saveAll(userTroops);

      enemyTroops = IntStream
          .range(0, 2)
          .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, enemyUser.getKingdom()))
          .collect(Collectors.toList());
      troopRepository.saveAll(enemyTroops);
    }

    @Test
    public void kingdomWithOutOfRangeLevelShouldThrowKingdomOutOfRangeException() throws Exception {
      Townhall townhall = (Townhall) buildingService
          .getBuildingsForKingdom(enemyUser.getKingdom())
          .stream()
          .filter(b -> b instanceof Townhall).findFirst().get();
      townhall.setLevel(5);
      buildingRepository.save(townhall);

      mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
              .header("Authorization", token))
          .andExpect(status().isForbidden())
          .andExpect(result -> assertTrue(
              result.getResolvedException() instanceof KingdomOutOfRangeException))
          .andExpect(
              result -> assertEquals(
                  "Kingdoms cannot battle with another out of range level kingdom",
                  result.getResolvedException().getMessage()));
    }

    @Test
    public void fightAgainstWeakerKingdomShouldResultInVictory() throws Exception {
      mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
              .header("Authorization", token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.result",
              is("Congratulation! " + user.getKingdom().getKingdomName() + " has conquered "
                  + enemyUser.getKingdom().getKingdomName())));
    }

    @Test
    public void fightAgainstStrongerKingdomShouldResultInDefeat() throws Exception {
      Resource userGold = resourceService.getGold(user.getKingdom());
      Resource userFood = resourceService.getFood(user.getKingdom());

      enemyTroops = IntStream
          .range(0, 5)
          .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, enemyUser.getKingdom()))
          .collect(Collectors.toList());
      troopRepository.saveAll(enemyTroops);

      mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
              .header("Authorization", token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.result",
              is("Game over, you've been conquered by " + enemyUser.getKingdom()
                  .getKingdomName())))
          .andExpect(jsonPath("$.opponentStatistics.earnedGold", is(userGold.getAmount())))
          .andExpect(jsonPath("$.opponentStatistics.earnedFood", is(userFood.getAmount())));
    }

    @Test
    public void fightAgainstKingdomWithNoTroopsShouldResultInVictory() throws Exception {
      troopRepository.deleteAll(enemyTroops);
      Resource enemyGold = resourceService.getGold(enemyUser.getKingdom());
      Resource enemyFood = resourceService.getFood(enemyUser.getKingdom());

      mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
              .header("Authorization", token))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.result",
              is("Congratulation! " + user.getKingdom().getKingdomName() + " has conquered "
                  + enemyUser.getKingdom().getKingdomName())))
          .andExpect(jsonPath("$.playerStatistics.earnedGold", is(enemyGold.getAmount())))
          .andExpect(jsonPath("$.playerStatistics.earnedFood", is(enemyFood.getAmount())))
          .andExpect(jsonPath("$.playerStatistics.lostBuildings",
              lessThanOrEqualTo(user.getKingdom().getBuildings().size())))
          .andExpect(jsonPath("$.playerStatistics.lostTroops",
              lessThanOrEqualTo(user.getKingdom().getTroops().size())))
          .andExpect(jsonPath("$.opponentStatistics.lostGold", is(enemyGold.getAmount())))
          .andExpect(jsonPath("$.opponentStatistics.lostFood", is(enemyFood.getAmount())))
          .andExpect(jsonPath("$.opponentStatistics.lostBuildings",
              lessThanOrEqualTo(enemyUser.getKingdom().getBuildings().size())))
          .andExpect(jsonPath("$.opponentStatistics.lostTroops",
              lessThanOrEqualTo(enemyUser.getKingdom().getTroops().size())));
    }

    @Test
    public void fightAgainstTheSameStrongKingdomShouldResultInDraw() throws Exception {
      enemyTroops = IntStream
          .range(0, 3)
          .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, enemyUser.getKingdom()))
          .collect(Collectors.toList());
      troopRepository.saveAll(enemyTroops);

      mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
              .header("Authorization", token))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.result", is("It's a draw, no kingdom wins")));
    }
  }

  @Test
  public void fourFiveHealthyTroopsAgainstTenUnhealthyTroopsShouldResultInVictory()
      throws Exception {
    final Resource enemyGold = resourceService.getGold(enemyUser.getKingdom());
    final Resource enemyFood = resourceService.getFood(enemyUser.getKingdom());

    userTroops = IntStream
        .range(0, 4)
        .mapToObj(i -> new Troop(1, 100, 50, 20, 1111L, 11111L, user.getKingdom()))
        .collect(Collectors.toList());
    troopRepository.saveAll(userTroops);

    enemyTroops = IntStream
        .range(0, 10)
        .mapToObj(i -> new Troop(1, 10, 50, 20, 1111L, 11111L, enemyUser.getKingdom()))
        .collect(Collectors.toList());
    troopRepository.saveAll(enemyTroops);

    mockMvc.perform(post("/kingdom/fight/" + enemyUser.getKingdom().getId())
            .header("Authorization", token))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result",
            is("Congratulation! " + user.getKingdom().getKingdomName() + " has conquered "
                + enemyUser.getKingdom().getKingdomName())))
        .andExpect(jsonPath("$.playerStatistics.earnedGold", is(enemyGold.getAmount())))
        .andExpect(jsonPath("$.playerStatistics.earnedFood", is(enemyFood.getAmount())))
        .andExpect(jsonPath("$.playerStatistics.lostBuildings",
            lessThanOrEqualTo(user.getKingdom().getBuildings().size())))
        .andExpect(jsonPath("$.playerStatistics.lostTroops",
            lessThanOrEqualTo(user.getKingdom().getTroops().size())))
        .andExpect(jsonPath("$.opponentStatistics.lostGold", is(enemyGold.getAmount())))
        .andExpect(jsonPath("$.opponentStatistics.lostFood", is(enemyFood.getAmount())))
        .andExpect(jsonPath("$.opponentStatistics.lostBuildings",
            lessThanOrEqualTo(enemyUser.getKingdom().getBuildings().size())))
        .andExpect(jsonPath("$.opponentStatistics.lostTroops",
            lessThanOrEqualTo(enemyUser.getKingdom().getTroops().size())));
  }
}
