package com.greenfoxacademy.zelenamackatribes.resources.services;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceGenerationChange;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import com.greenfoxacademy.zelenamackatribes.resources.models.resources.Food;
import com.greenfoxacademy.zelenamackatribes.resources.models.resources.Gold;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceGenerationChangeRepository;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements ResourceService {

  private final ResourceRepository resourceRepository;
  private final ResourceGenerationChangeRepository resourceGenerationChangeRepository;
  private final TimeService timeService;

  @Autowired
  public ResourceServiceImpl(ResourceRepository resourceRepository,
      ResourceGenerationChangeRepository resourceGenerationChangeRepository,
      TimeService timeService) {
    this.resourceRepository = resourceRepository;
    this.resourceGenerationChangeRepository = resourceGenerationChangeRepository;
    this.timeService = timeService;
  }

  @Override
  public List<Resource> getResources(Kingdom kingdom)
      throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException {
    List<Resource> resources = resourceRepository.findResourcesByKingdom(kingdom);
    if (resources.size() != ResourceType.values().length) {
      throw new InvalidNumberOfResourceObjectsException(
          String.format("Unexpected number of resources found (wants=%1$d, found=%2$d)",
              ResourceType.values().length, resources.size()));
    }
    validateResources(resources);
    updateResourceGeneration(resources);
    return resources;
  }

  @Override
  public void saveAllResources(List<Resource> resources) {
    resourceRepository.saveAll(resources);
  }

  @Override
  public void handlePurchase(Kingdom kingdom, int goldAmount, int foodAmount)
      throws InvalidNumberOfResourceObjectsException, NotEnoughResourcesException,
      ResourceNotFoundException {
    if (goldAmount < 0 || foodAmount < 0) {
      throw new InputMismatchException("Amount cannot be negative");
    }
    List<Resource> resources = getResources(kingdom);
    Resource gold = getResource(resources, ResourceType.GOLD);
    Resource food = getResource(resources, ResourceType.FOOD);

    validateResourceAmounts(gold, goldAmount, food, foodAmount);

    gold.setAmount(gold.getAmount() - goldAmount);
    gold.setUpdatedAt(timeService.getTime());
    food.setAmount(food.getAmount() - foodAmount);
    food.setUpdatedAt(timeService.getTime());
    resourceRepository.save(gold);
    resourceRepository.save(food);
  }

  private void validateResourceAmounts(Resource gold, int goldAmount, Resource food, int foodAmount)
      throws NotEnoughResourcesException {
    boolean isEnoughGold = gold.getAmount() >= goldAmount;
    boolean isEnoughFood = food.getAmount() >= foodAmount;

    if (!isEnoughGold || !isEnoughFood) {
      String message = "Don't have enough ";
      if (!isEnoughGold && !isEnoughFood) {
        message += "gold and food";
      } else if (!isEnoughGold) {
        message += "gold";
      } else {
        message += "food";
      }
      throw new NotEnoughResourcesException(message);
    }
  }

  private void validateResources(List<Resource> resources) throws ResourceNotFoundException {
    getResource(resources, ResourceType.GOLD);
    getResource(resources, ResourceType.FOOD);
  }

  @Override
  public Resource getResource(List<Resource> resources, ResourceType resourceType)
      throws ResourceNotFoundException {
    return resources
        .stream()
        .filter(r -> r.getType().equals(resourceType))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(String.format("%1$s not found",
            resourceType.name().charAt(0) + resourceType.name().substring(1).toLowerCase())));
  }

  @Override
  public Resource getResource(Kingdom kingdom, ResourceType resourceType)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    return getResource(getResources(kingdom), resourceType);
  }

  @Override
  public Gold getGold(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    return new Gold(getResource(kingdom, ResourceType.GOLD));
  }

  @Override
  public Food getFood(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException {
    return new Food(getResource(kingdom, ResourceType.FOOD));
  }

  @Override
  public void removeByKingdom(Kingdom kingdom) {
    resourceGenerationChangeRepository.deleteAllByResourceIn(
        resourceRepository.findResourcesByKingdom(kingdom));
    resourceRepository.deleteAllByKingdom(kingdom);
  }

  @Override
  public void addGenerationChange(Kingdom kingdom, ResourceType type, int amount, long changeAt) {
    var resource = resourceRepository.findResourcesByKingdom(kingdom).stream()
        .filter(r -> r.getType().equals(type)).findFirst().get();
    resourceGenerationChangeRepository.save(
        new ResourceGenerationChange(null, type, amount, resource, changeAt));
  }

  @Override
  public void applyGenerationChange(Kingdom kingdom) {
    updateResourceGeneration(resourceRepository.findResourcesByKingdom(kingdom));
  }

  @Override
  public void applyResourceGeneration() {
    updateResourceGeneration(resourceRepository.findAll());
    addResourcePerTick(resourceRepository.findAll());
  }

  private void updateResourceGeneration(List<Resource> resources) {
    var time = timeService.getTime();
    var currentChanges =
        resourceGenerationChangeRepository.findAllByResourceInAndChangeAtIsLessThanEqual(
        resources, time);
    var changeValues = currentChanges.stream()
        .collect(Collectors.groupingBy((change -> change.getResource().getId()),
            Collectors.summingInt(ResourceGenerationChange::getAmount)));
    for (var resource : resources) {
      var change = changeValues.get(resource.getId());
      if (change != null && change != 0) {
        resource.setGeneration(resource.getGeneration() + changeValues.get(resource.getId()));
        resource.setUpdatedAt(time);
      }
    }
    resourceRepository.saveAll(resources);
    resourceGenerationChangeRepository.deleteAll(currentChanges);
  }

  private void addResourcePerTick(List<Resource> resources) {
    for (var resource : resources) {
      resource.setAmount(resource.getAmount() + resource.getGeneration());
    }
    resourceRepository.saveAll(resources);
  }
}
