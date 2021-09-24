package com.greenfoxacademy.zelenamackatribes.resources.services;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.models.resources.Food;
import com.greenfoxacademy.zelenamackatribes.resources.models.resources.Gold;
import java.util.InputMismatchException;
import java.util.List;

public interface ResourceService {

  List<Resource> getResources(Kingdom kingdom)
      throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException;

  void saveAllResources(List<Resource> resources);

  void handlePurchase(Kingdom kingdom, int goldAmount, int foodAmount)
      throws InputMismatchException, InvalidNumberOfResourceObjectsException,
      NotEnoughResourcesException, ResourceNotFoundException;

  Resource getResource(List<Resource> resources, ResourceType resourceType)
      throws ResourceNotFoundException;

  Resource getResource(Kingdom kingdom, ResourceType resourceType)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException;

  Gold getGold(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException;

  Food getFood(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException;

  void removeByKingdom(Kingdom kingdom);

  void addGenerationChange(Kingdom kingdom, ResourceType type, int amount, long changeAt);

  void applyGenerationChange(Kingdom kingdom);

  void applyResourceGeneration();
}
