package com.greenfoxacademy.zelenamackatribes.kingdoms.services;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomScoreDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.LeaderboardPageDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.IncorrectPageParameterException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.KingdomScore;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomScoreRepository;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

  private final BuildingService buildingService;
  private final KingdomRepository kingdomRepository;
  private final KingdomScoreRepository kingdomScoreRepository;
  private final ResourceService resourceService;
  private final TroopService troopService;
  private final TimeService timeService;

  public LeaderboardServiceImpl(
      BuildingService buildingService, KingdomRepository kingdomRepository,
      KingdomScoreRepository kingdomScoreRepository, ResourceService resourceService,
      TroopService troopService, TimeService timeService) {
    this.buildingService = buildingService;
    this.kingdomRepository = kingdomRepository;
    this.kingdomScoreRepository = kingdomScoreRepository;
    this.resourceService = resourceService;
    this.troopService = troopService;
    this.timeService = timeService;
  }

  @Override
  public LeaderboardPageDTO createLeaderboard(Integer pageNo, Integer pageSize, String scoreType,
      boolean isHistory)
      throws IncorrectPageParameterException, InvalidNumberOfResourceObjectsException,
      ResourceNotFoundException {
    validateCreateLeaderboardInput(pageNo, pageSize, scoreType);
    recountKingdomScores();
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(scoreType).descending());
    return new LeaderboardPageDTO(
        kingdomScoreRepository.findByIsHistory(isHistory, pageable),
        scoreType);
  }

  @Override
  public void recountKingdomScores()
      throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException {
    List<Kingdom> kingdoms = kingdomRepository.findAll();
    List<KingdomScore> scores = kingdomScoreRepository.findAll();
    for (Kingdom kingdom : kingdoms) {
      List<KingdomScore> actualScores = scores.stream()
          .filter(score -> score.getKingdom() != null)
          .filter(score -> score.getKingdom().getId().equals(kingdom.getId()))
          .collect(Collectors.toList());

      KingdomScore currentScore = actualScores.stream()
          .filter(score -> !score.isHistory())
          .findFirst().orElse(new KingdomScore());
      int buildingsScore = countBuildingsScore(kingdom);
      currentScore.setBuildingsScore(buildingsScore);
      int troopsScore = countTroopsScore(kingdom);
      currentScore.setTroopsScore(troopsScore);
      int resourcesScore = countResourcesScore(kingdom);
      currentScore.setResourcesScore(resourcesScore);
      updateTotalScore(currentScore);

      KingdomScore historyScore = actualScores.stream()
          .filter(KingdomScore::isHistory)
          .findFirst().orElse(new KingdomScore());
      if (historyScore.getBuildingsScore() < buildingsScore) {
        historyScore.setBuildingsScore(buildingsScore);
      }
      if (historyScore.getTroopsScore() < troopsScore) {
        historyScore.setTroopsScore(troopsScore);
      }
      if (historyScore.getResourcesScore() < resourcesScore) {
        historyScore.setResourcesScore(resourcesScore);
      }
      if (historyScore.getTotalScore() < currentScore.getTotalScore()) {
        historyScore.setTotalScore(currentScore.getTotalScore());
      }
    }
    kingdomScoreRepository.saveAll(scores);
  }

  @Override
  public void updateScore(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    KingdomScore kingdomScore = kingdomScoreRepository.findByKingdom(kingdom)
        .orElse(new KingdomScore());
    kingdomScore.setKingdom(kingdom);
    int buildingsScore = countBuildingsScore(kingdom);
    kingdomScore.setBuildingsScore(buildingsScore);
    int troopsScore = countTroopsScore(kingdom);
    kingdomScore.setTroopsScore(troopsScore);
    int resourcesScore = countResourcesScore(kingdom);
    kingdomScore.setResourcesScore(resourcesScore);
    updateTotalScore(kingdomScore);
    kingdomScoreRepository.save(kingdomScore);
  }

  private void validateCreateLeaderboardInput(Integer pageNo, Integer pageSize, String scoreType)
      throws IncorrectPageParameterException {
    int numberOfKingdoms = kingdomRepository.findAll().size();
    if (numberOfKingdoms <= pageNo * pageSize) {
      throw new IncorrectPageParameterException("Not enough kingdoms to display given pageNo");
    }
    if (pageNo < 0) {
      throw new IncorrectPageParameterException("Parameter pageNo must be a positive integer");
    }
    if (pageSize <= 0) {
      throw new IncorrectPageParameterException("Parameter pageSize must be a positive integer");
    }
    if (!Arrays.stream(KingdomScoreDTO.class.getDeclaredFields())
        .map(Field::getName)
        .collect(Collectors.toList())
        .contains(scoreType)) {
      throw new IncorrectPageParameterException("Parameter scoreType is not of a given type");
    }
  }

  private int countTroopsScore(Kingdom kingdom) {
    List<Troop> troops = troopService.getTroopsForKingdom(kingdom);
    int score = 0;
    for (Troop troop : troops) {
      if (troop.getFinishedAt() <= timeService.getTime()) {
        score += troop.getLevel();
      }
    }
    return score;
  }

  private int countBuildingsScore(Kingdom kingdom) {
    List<Building> buildings = buildingService.getBuildingsForKingdom(kingdom);
    int score = 0;
    for (Building building : buildings) {
      if (building.getFinishedAt() <= timeService.getTime()) {
        score += building.getLevel();
      }
    }
    return score;
  }

  private int countResourcesScore(Kingdom kingdom)
      throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException {
    List<Resource> resources = resourceService.getResources(kingdom);
    int score = 0;
    for (Resource resource : resources) {
      score += resource.getGeneration();
    }
    return score;
  }

  private void updateTotalScore(KingdomScore score) {
    score.setTotalScore(
        score.getBuildingsScore() + score.getTroopsScore() + score.getResourcesScore());
  }
}
