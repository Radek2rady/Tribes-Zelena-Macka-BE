package com.greenfoxacademy.zelenamackatribes.kingdoms.services;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.buildings.Townhall;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomStatsDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomsStatsResponseDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomHasNoTownhallException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomOutOfRangeException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class BattleServiceImpl implements BattleService {

  private final BuildingService buildingService;
  private final KingdomService kingdomService;
  private final ResourceService resourceService;
  private final TroopService troopService;

  public BattleServiceImpl(BuildingService buildingService, KingdomService kingdomService,
      ResourceService resourceService, TroopService troopService) {
    this.buildingService = buildingService;
    this.kingdomService = kingdomService;
    this.resourceService = resourceService;
    this.troopService = troopService;
  }

  @Override
  public KingdomsStatsResponseDTO battle(Long playerKingdomId, Long enemyKingdomId)
      throws KingdomNotFoundException, ForbiddenActionException, KingdomHasNoTownhallException,
      ResourceNotFoundException, KingdomOutOfRangeException,
      InvalidNumberOfResourceObjectsException, DefaultValuesFileException {
    if (enemyKingdomId.equals(playerKingdomId)) {
      throw new ForbiddenActionException("Kingdom is not able to fight with itself");
    }
    Kingdom playerKingdom = getFullKingdom(playerKingdomId);
    Kingdom enemyKingdom = getFullKingdom(enemyKingdomId);
    if (playerKingdom.getTroops().isEmpty()) {
      throw new ForbiddenActionException("A kingdom without troops can't initiate a battle");
    }
    validateKingdomsLevels(getTownhall(playerKingdom).getLevel(),
        getTownhall(enemyKingdom).getLevel());
    while (haveKingdomsTownhall(playerKingdom, enemyKingdom)
        && isSomeTroopAlive(playerKingdom, enemyKingdom)) {
      List<Troop> playerTroops = playerKingdom.getTroops();
      List<Troop> enemyTroops = enemyKingdom.getTroops();
      int sizeDiff = playerTroops.size() - enemyTroops.size();
      List<Troop> troopsWithNoOpponent = new ArrayList<>();
      if (sizeDiff != 0) {
        troopsWithNoOpponent = getTroopsWithNoOpponent(playerTroops, enemyTroops,
            sizeDiff);
        List<Building> buildingsToAttack = getBuildingsToAttack(playerKingdom, enemyKingdom,
            troopsWithNoOpponent);
        attackBuildings(troopsWithNoOpponent, buildingsToAttack);
      }
      if (!playerTroops.isEmpty() && !enemyTroops.isEmpty()) {
        executeTroopFight(playerTroops, enemyTroops);
      }
      reUniteTroops(troopsWithNoOpponent, playerTroops, enemyTroops, playerKingdomId);
    }
    Map<Long, Map<String, Integer>> initStats = getInitKingdomsStats(playerKingdom, enemyKingdom);
    return evaluateBattle(playerKingdom, enemyKingdom, initStats);
  }

  private KingdomsStatsResponseDTO evaluateBattle(Kingdom playerKingdom, Kingdom enemyKingdom,
      Map<Long, Map<String, Integer>> initStats)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    if (buildingService.hasTownhall(playerKingdom) && !playerKingdom.getTroops().isEmpty()) {
      return new KingdomsStatsResponseDTO(
          String.format("Congratulation! %s has conquered %s", playerKingdom.getKingdomName(),
              enemyKingdom.getKingdomName()),
          getWinnerStats(playerKingdom, enemyKingdom, initStats),
          handleBattleResult(playerKingdom, enemyKingdom, initStats));
    } else if (buildingService.hasTownhall(enemyKingdom) && !enemyKingdom.getTroops().isEmpty()) {
      return new KingdomsStatsResponseDTO(String
          .format("Game over, you've been conquered by %s", enemyKingdom.getKingdomName()),
          handleBattleResult(enemyKingdom, playerKingdom, initStats),
          getWinnerStats(enemyKingdom, playerKingdom, initStats));
    }
    return new KingdomsStatsResponseDTO("It's a draw, no kingdom wins",
        getDrawStats(playerKingdom, initStats), getDrawStats(enemyKingdom, initStats));
  }

  private Map<Long, Map<String, Integer>> getInitKingdomsStats(Kingdom playerKingdom,
      Kingdom enemyKingdom) {
    Map<String, Integer> playerMap = new HashMap<>();
    playerMap.put("buildings", playerKingdom.getBuildings().size());
    playerMap.put("troops", playerKingdom.getTroops().size());
    playerMap.put("food", playerKingdom.getResources().stream().filter(r -> r.getType().equals(
        ResourceType.FOOD)).findFirst().get().getAmount());
    playerMap.put("gold", playerKingdom.getResources().stream().filter(r -> r.getType().equals(
        ResourceType.GOLD)).findFirst().get().getAmount());
    Map<String, Integer> enemyMap = new HashMap<>();
    enemyMap.put("buildings", enemyKingdom.getBuildings().size());
    enemyMap.put("troops", enemyKingdom.getTroops().size());
    enemyMap.put("food", enemyKingdom.getResources().stream().filter(r -> r.getType().equals(
        ResourceType.FOOD)).findFirst().get().getAmount());
    enemyMap.put("gold", enemyKingdom.getResources().stream().filter(r -> r.getType().equals(
        ResourceType.GOLD)).findFirst().get().getAmount());
    Map<Long, Map<String, Integer>> kingdomsMap = new HashMap<>();
    kingdomsMap.put(playerKingdom.getId(), playerMap);
    kingdomsMap.put(enemyKingdom.getId(), enemyMap);
    return kingdomsMap;
  }

  private void reUniteTroops(List<Troop> troopsWithNoOpponent, List<Troop> playerTroops,
      List<Troop> enemyTroops, Long playerKingdomId) {
    if (!troopsWithNoOpponent.isEmpty()) {
      if (troopsWithNoOpponent.get(0).getKingdom().getId().equals(playerKingdomId)) {
        playerTroops.addAll(troopsWithNoOpponent);
      } else {
        enemyTroops.addAll(troopsWithNoOpponent);
      }
    }
  }

  private KingdomStatsDTO handleBattleResult(Kingdom winnerKingdom, Kingdom loserKingdom,
      Map<Long, Map<String, Integer>> initStats)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    KingdomStatsDTO loserKingdomStats = getLoserStats(loserKingdom, initStats);
    allocateResources(winnerKingdom, loserKingdom);
    kingdomService.removeKingdom(loserKingdom);
    return loserKingdomStats;
  }

  private int getLostBuildings(Kingdom kingdom, Map<Long, Map<String, Integer>> initStats) {
    return initStats.get(kingdom.getId()).get("buildings") - kingdom.getBuildings().size();
  }

  private int getLostTroops(Kingdom kingdom, Map<Long, Map<String, Integer>> initStats) {
    return initStats.get(kingdom.getId()).get("troops") - kingdom.getTroops().size();
  }

  private KingdomStatsDTO getWinnerStats(Kingdom winner, Kingdom loser,
      Map<Long, Map<String, Integer>> initStats) {
    return new KingdomStatsDTO(
        getLostBuildings(winner, initStats),
        getLostTroops(winner, initStats),
        null,
        null,
        initStats.get(loser.getId()).get("gold"),
        initStats.get(loser.getId()).get("food"));
  }

  private KingdomStatsDTO getLoserStats(Kingdom loser,
      Map<Long, Map<String, Integer>> initStats) {
    return new KingdomStatsDTO(
        getLostBuildings(loser, initStats),
        getLostTroops(loser, initStats),
        initStats.get(loser.getId()).get("gold"),
        initStats.get(loser.getId()).get("food"),
        null,
        null);
  }

  private KingdomStatsDTO getDrawStats(Kingdom kingdom,
      Map<Long, Map<String, Integer>> initStats) {
    return new KingdomStatsDTO(
        getLostBuildings(kingdom, initStats),
        getLostTroops(kingdom, initStats));
  }

  private void validateKingdomsLevels(int playerKingdomLevel, int enemyKingdomLevel)
      throws KingdomOutOfRangeException {
    if (Math.abs(playerKingdomLevel - enemyKingdomLevel) > 3) {
      throw new KingdomOutOfRangeException(
          "Kingdoms cannot battle with another out of range level kingdom");
    }
  }

  private Building getTownhall(Kingdom kingdom) throws KingdomHasNoTownhallException {
    return buildingService.getBuildingsForKingdom(kingdom)
        .stream()
        .filter(b -> b instanceof Townhall)
        .findFirst()
        .orElseThrow(() -> new KingdomHasNoTownhallException("Kingdom has no Townhall"));
  }

  private boolean haveKingdomsTownhall(Kingdom playerKingdom, Kingdom enemyKingdom) {
    return buildingService.hasTownhall(playerKingdom) && buildingService.hasTownhall(enemyKingdom);
  }

  private boolean isSomeTroopAlive(Kingdom playerKingdom, Kingdom enemyKingdom) {
    return !playerKingdom.getTroops().isEmpty() || !enemyKingdom.getTroops().isEmpty();
  }

  private List<Building> getBuildingsToAttack(Kingdom playerKingdom, Kingdom enemyKingdom,
      List<Troop> remainingTroops) {
    if (remainingTroops.get(0).getKingdom().getId().equals(enemyKingdom.getId())) {
      return playerKingdom.getBuildings();
    }
    return enemyKingdom.getBuildings();
  }

  private List<Troop> getTroopsWithNoOpponent(List<Troop> playerTroops, List<Troop> enemyTroops,
      int sizeDiff) {
    if (sizeDiff < 0) {
      sizeDiff *= -1;
      return troopsWithNoOpponent(sizeDiff, enemyTroops);
    }
    return troopsWithNoOpponent(sizeDiff, playerTroops);
  }

  private List<Troop> troopsWithNoOpponent(int sizeDiff, List<Troop> originalTroops) {
    List<Troop> troopsWithNoOpponent = new ArrayList<>();
    while (sizeDiff > 0) {
      Troop troop = originalTroops.get(getRandomIndex(originalTroops.size()));
      troopsWithNoOpponent.add(troop);
      originalTroops.remove(troop);
      sizeDiff--;
    }
    return troopsWithNoOpponent;
  }

  private void executeTroopFight(List<Troop> playerTroops, List<Troop> enemyTroops) {
    while (playerTroops.size() > 0 && enemyTroops.size() > 0) {
      Troop playerTroop = playerTroops.get(getRandomIndex(playerTroops.size()));
      Troop enemyTroop = enemyTroops.get(getRandomIndex(enemyTroops.size()));
      startAttack(playerTroop, enemyTroop, playerTroops, enemyTroops);
    }
  }

  private void startAttack(Troop playerTroop, Troop enemyTroop, List<Troop> playerTroops,
      List<Troop> enemyTroops) {
    int index = getRandomIndex(2);
    if (index == 0) {
      attackTroop(playerTroop, enemyTroop);
    } else {
      attackTroop(enemyTroop, playerTroop);
    }
    if (playerTroop.getHp() < 1) {
      playerTroops.remove(playerTroop);
      troopService.remove(playerTroop);
    }
    if (enemyTroop.getHp() < 1) {
      enemyTroops.remove(enemyTroop);
      troopService.remove(enemyTroop);
    }
  }

  private void attackTroop(Troop attackingTroop, Troop defendingTroop) {
    int damage = attackingTroop.getAttack() - defendingTroop.getDefence();
    if (damage > 0) {
      defendingTroop.setHp(defendingTroop.getHp() - damage);
    } else {
      defendingTroop.setHp(defendingTroop.getHp() - 1);
    }
    troopService.update(defendingTroop);
    if (defendingTroop.getHp() > 0) {
      attackTroop(defendingTroop, attackingTroop);
    }
  }

  private void attackBuildings(List<Troop> attackingTroops, List<Building> buildingsToAttack)
      throws DefaultValuesFileException {
    for (int i = attackingTroops.size() - 1; i >= 0; i--) {
      Troop attackingTroop = attackingTroops.get(i);
      if (buildingsToAttack.isEmpty()) {
        break;
      }
      Building buildingToAttack = getBuildingToAttack(buildingsToAttack);
      buildingToAttack.setHp(buildingToAttack.getHp() - attackingTroop.getAttack());
      buildingService.update(buildingToAttack);
      if (buildingToAttack.getHp() < 1) {
        buildingsToAttack.remove(buildingToAttack);
        buildingService.remove(buildingToAttack);
      }
      attackingTroop.setHp(attackingTroop.getHp() - 10);
      troopService.update(attackingTroop);
      if (attackingTroop.getHp() < 1) {
        attackingTroops.remove(attackingTroop);
        troopService.remove(attackingTroop);
      }
    }
  }

  private Building getBuildingToAttack(List<Building> buildingsToAttack) {
    Building buildingToAttack;
    if (buildingsToAttack.size() == 1) {
      buildingToAttack = buildingsToAttack.get(0);
    } else {
      buildingToAttack = buildingsToAttack.get(getRandomIndex(buildingsToAttack.size()));
      while (buildingToAttack instanceof Townhall) {
        buildingToAttack = buildingsToAttack.get(getRandomIndex(buildingsToAttack.size()));
      }
    }
    return buildingToAttack;
  }

  private int getRandomIndex(int bound) {
    Random rdm = new Random();
    return rdm.nextInt(bound);
  }

  private Kingdom getFullKingdom(long playerKingdomId) throws KingdomNotFoundException {
    Kingdom kingdom = kingdomService.getById(playerKingdomId);
    kingdom.setBuildings(buildingService.getBuildingsForKingdom(kingdom));
    kingdom.setTroops(troopService.getTroopsByKingdomId(playerKingdomId));
    return kingdom;
  }

  private void allocateResources(Kingdom winner, Kingdom loser)
      throws ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException {
    var winnerResources = resourceService.getResources(winner);
    var loserResources = resourceService.getResources(loser);
    for (var addTo : winnerResources) {
      var takeFrom = resourceService.getResource(loserResources, addTo.getType());
      addTo.setAmount(addTo.getAmount() + takeFrom.getAmount());
      takeFrom.setAmount(0);
    }
    resourceService.saveAllResources(winnerResources);
  }
}

