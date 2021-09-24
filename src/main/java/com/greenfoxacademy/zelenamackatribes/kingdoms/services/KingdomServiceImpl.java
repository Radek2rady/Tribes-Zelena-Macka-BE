package com.greenfoxacademy.zelenamackatribes.kingdoms.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.KingdomScore;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomScoreRepository;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import java.util.ArrayList;
import org.springframework.stereotype.Service;

@Service
public class KingdomServiceImpl implements KingdomService {

  private final BuildingService buildingService;
  private final KingdomRepository kingdomRepository;
  private final KingdomScoreRepository kingdomScoreRepository;
  private final ResourceService resourceService;
  private final TroopService troopService;
  private final LeaderboardService leaderboardService;

  public KingdomServiceImpl(
      BuildingService buildingService, KingdomRepository kingdomRepository,
      KingdomScoreRepository kingdomScoreRepository, ResourceService resourceService,
      TroopService troopService, LeaderboardService leaderboardService) {
    this.buildingService = buildingService;
    this.kingdomRepository = kingdomRepository;
    this.kingdomScoreRepository = kingdomScoreRepository;
    this.resourceService = resourceService;
    this.troopService = troopService;
    this.leaderboardService = leaderboardService;
  }

  @Override
  public Kingdom getByName(String kingdomName) throws KingdomNotFoundException {
    return kingdomRepository.findKingdomByKingdomName(kingdomName).orElseThrow(
        () -> new KingdomNotFoundException(
            String.format("Could not find kingdom named %1$s", kingdomName)));
  }

  @Override
  public Kingdom getById(Long kingdomId) throws KingdomNotFoundException {
    return kingdomRepository.findById(kingdomId).orElseThrow(
        () -> new KingdomNotFoundException("Kingdom id not found"));
  }

  @Override
  public Kingdom getKingdomForUser(UserEntity user) throws KingdomNotFoundException {
    return kingdomRepository.findKingdomByUser(user).orElseThrow(
        () -> new KingdomNotFoundException(
            String.format("Could not find kingdom owned by %1$s (Id=%2$d)", user.getUsername(),
                user.getId())));
  }

  @Override
  public Kingdom createKingdom(UserEntity user, String name)
      throws DefaultValuesFileException, InvalidBuildingTypeException,
      NotEnoughResourcesException, ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException {
    var buildings = new ArrayList<Building>();
    var resources = new ArrayList<Resource>();
    var troops = new ArrayList<Troop>();
    var scores = new ArrayList<KingdomScore>();
    var kingdom = new Kingdom(null, name, user, buildings, resources, troops, scores);
    kingdomRepository.save(kingdom);
    resources.add(new Resource(
        null,
        ResourceType.GOLD,
        DefaultVals.getInt("starterPack.resources.gold"),
        kingdom,
        0,
        0L
    ));
    resources.add(new Resource(
        null,
        ResourceType.FOOD,
        DefaultVals.getInt("starterPack.resources.food"),
        kingdom,
        0,
        0L
    ));
    resourceService.saveAllResources(resources);
    for (var item : DefaultVals.getStringList("starterPack.buildings")) {
      buildingService.createBuilding(BuildingType.valueOf(item.toUpperCase()), kingdom, true);
    }
    kingdom.setResources(resourceService.getResources(kingdom));
    user.setKingdom(kingdom);
    KingdomScore score = new KingdomScore(kingdom,false);
    KingdomScore historyScore = new KingdomScore(kingdom, true);
    scores.add(score);
    scores.add(historyScore);
    kingdom.setScores(scores);
    kingdomScoreRepository.saveAll(scores);
    leaderboardService.recountKingdomScores();
    return kingdom;
  }

  @Override
  public void removeKingdom(Kingdom kingdom) {
    troopService.removeByKingdom(kingdom);
    buildingService.removeByKingdom(kingdom);
    resourceService.removeByKingdom(kingdom);
    KingdomScore score = kingdomScoreRepository.findByKingdomAndIsHistory(kingdom, false);
    kingdomScoreRepository.delete(score);
    KingdomScore historyScore = kingdomScoreRepository.findByKingdomAndIsHistory(kingdom, true);
    historyScore.setKingdom(null);
    kingdomScoreRepository.save(historyScore);
    kingdomRepository.delete(kingdom);
  }

  @Override
  public boolean getKingdomByNameFromString(String kingdomName) {
    return kingdomRepository.findKingdomByKingdomName(kingdomName).isPresent();
  }
}
