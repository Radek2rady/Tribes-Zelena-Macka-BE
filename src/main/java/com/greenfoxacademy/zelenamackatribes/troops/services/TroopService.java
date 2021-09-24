package com.greenfoxacademy.zelenamackatribes.troops.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidAcademyException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.InvalidTroopUpgradeException;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.TroopNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import java.util.InputMismatchException;
import java.util.List;

public interface TroopService {

  Troop createTroop(Building building)
      throws InvalidAcademyException, NotEnoughResourcesException,
      InvalidNumberOfResourceObjectsException,
      ResourceNotFoundException, InputMismatchException, DefaultValuesFileException;

  List<Troop> getTroopsByKingdomId(Long kingdomId);

  Troop getTroopById(long troopId) throws TroopNotFoundException;

  Troop getTroop(long troopId, Long kingdomId)
      throws ForbiddenActionException, TroopNotFoundException;

  void update(Troop troop);

  void remove(Troop troop);

  void removeByKingdom(Kingdom kingdom);

  Troop upgrade(Kingdom kingdom, Troop troop, Building academy)
      throws ForbiddenActionException, InvalidTroopUpgradeException, InvalidAcademyException,
      NotEnoughResourcesException, ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException, DefaultValuesFileException;

  List<Troop> getTroopsForKingdom(Kingdom kingdom);
}
