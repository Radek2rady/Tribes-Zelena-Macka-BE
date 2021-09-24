package com.greenfoxacademy.zelenamackatribes.kingdoms.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;

public interface KingdomService {

  Kingdom getByName(String kingdomName) throws KingdomNotFoundException;

  Kingdom getKingdomForUser(UserEntity user) throws KingdomNotFoundException;

  Kingdom getById(Long kingdomId) throws KingdomNotFoundException;

  Kingdom createKingdom(UserEntity user, String name)
      throws DefaultValuesFileException, InvalidBuildingTypeException,
      NotEnoughResourcesException, ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException;

  void removeKingdom(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException;

  boolean getKingdomByNameFromString(String kingdomName);
}
