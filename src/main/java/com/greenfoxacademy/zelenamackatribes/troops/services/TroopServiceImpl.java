package com.greenfoxacademy.zelenamackatribes.troops.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidAcademyException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.models.BuildingType;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.InvalidTroopUpgradeException;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.TroopNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.repositories.TroopRepository;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.InputMismatchException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TroopServiceImpl implements TroopService {

  private final TroopRepository troopRepository;
  private final TimeService timeService;
  private final ResourceService resourceService;

  @Autowired
  public TroopServiceImpl(TroopRepository troopRepository, TimeService timeService,
      ResourceService resourceService) {
    this.troopRepository = troopRepository;
    this.timeService = timeService;
    this.resourceService = resourceService;
  }

  @Override
  public Troop createTroop(Building academy)
      throws InvalidAcademyException, NotEnoughResourcesException,
      InvalidNumberOfResourceObjectsException,
      ResourceNotFoundException, InputMismatchException, DefaultValuesFileException {
    var time = timeService.getTime();
    if (!(academy.getType().equals(BuildingType.ACADEMY)) || academy.getFinishedAt() > time) {
      throw new InvalidAcademyException("Not a valid academy id");
    }
    if (troopRepository.countAllByAcademyAndFinishedAtGreaterThan(academy, time) >= 5) {
      throw new NotEnoughResourcesException("Not enough space in academy");
    }
    var kingdom = academy.getKingdom();
    var level = academy.getLevel();
    resourceService.handlePurchase(
        kingdom,
        level * DefaultVals.getInt("troops.troop.price"),
        0
    );
    var finishedAt = timeService.getTimeAfter(
        level * DefaultVals.getInt("troops.troop.trainingTimeCoeficient"));
    Troop troop = new Troop(level, time, finishedAt, kingdom, academy);
    resourceService.addGenerationChange(troop.getKingdom(), ResourceType.FOOD,
        -5 * level, troop.getFinishedAt());
    troopRepository.save(troop);
    return troop;
  }

  @Override
  public List<Troop> getTroopsByKingdomId(Long kingdomId) {
    return troopRepository.findAllByKingdomId(kingdomId);
  }

  @Override
  public Troop getTroopById(long troopId) throws TroopNotFoundException {
    return troopRepository
        .findById(troopId)
        .orElseThrow(() -> new TroopNotFoundException("Troop id not found"));
  }

  @Override
  public Troop getTroop(long troopId, Long kingdomId)
      throws ForbiddenActionException, TroopNotFoundException {
    Troop troop = getTroopById(troopId);
    if (!troop.getKingdom().getId().equals(kingdomId)) {
      throw new ForbiddenActionException("Forbidden action");
    } else {
      return troop;
    }
  }

  @Override
  public void update(Troop troop) {
    troopRepository.save(troop);
  }

  @Override
  public void remove(Troop troop) {
    resourceService.addGenerationChange(troop.getKingdom(), ResourceType.FOOD,
        5 * troop.getLevel(), timeService.getTime());
    troopRepository.delete(troop);
  }

  @Override
  public void removeByKingdom(Kingdom kingdom) {
    troopRepository.deleteAllByKingdom(kingdom);
  }

  @Override
  public Troop upgrade(Kingdom kingdom, Troop troop, Building academy)
      throws ForbiddenActionException, InvalidTroopUpgradeException, InvalidAcademyException,
      NotEnoughResourcesException, ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException, DefaultValuesFileException {
    validateUpgrade(kingdom, troop, academy);
    final var level = academy.getLevel();
    final var levelDiff = academy.getLevel() - troop.getLevel();
    resourceService.handlePurchase(kingdom, DefaultVals.getInt("troops.troop.price") * levelDiff,
        0);
    troop.setAcademy(academy);
    troop.setStartedAt(timeService.getTime());
    troop.setFinishedAt(timeService.getTimeAfter(
        DefaultVals.getInt("troops.troop.trainingTimeCoeficient") * levelDiff));
    troop.setLevel(level);
    troop.setHp(DefaultVals.getInt("troops.troop.defaultHp") * level);
    troop.setAttack(DefaultVals.getInt("troops.troop.defaultAttack") * level);
    troop.setDefence(DefaultVals.getInt("troops.troop.defaultDefence") * level);
    resourceService.addGenerationChange(troop.getKingdom(), ResourceType.FOOD,
        -5 * levelDiff, troop.getFinishedAt());
    troopRepository.save(troop);
    return troop;
  }

  private void validateUpgrade(Kingdom kingdom, Troop troop, Building academy)
      throws NotEnoughResourcesException, InvalidTroopUpgradeException, InvalidAcademyException,
      ForbiddenActionException {
    if (!(academy.getKingdom().getId().equals(kingdom.getId()) && troop.getKingdom().getId()
        .equals(kingdom.getId()))) {
      throw new ForbiddenActionException("Forbidden action");
    }
    if (!academy.getType().equals(BuildingType.ACADEMY)) {
      throw new InvalidAcademyException("Invalid academy id");
    }
    if (academy.getFinishedAt() > timeService.getTime()) {
      throw new InvalidAcademyException("Cannot use unfinished academy for upgrade");
    }
    if (troop.getLevel() >= academy.getLevel()) {
      throw new InvalidTroopUpgradeException("Cannot upgrade troop above academy level");
    }
    if (troopRepository.countAllByAcademyAndFinishedAtGreaterThan(academy, timeService.getTime())
        >= 5) {
      throw new NotEnoughResourcesException("Not enough space in academy");
    }
  }

  @Override
  public List<Troop> getTroopsForKingdom(Kingdom kingdom) {
    return troopRepository.findAllByKingdom(kingdom);
  }
}
