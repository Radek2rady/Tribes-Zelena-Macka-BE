package com.greenfoxacademy.zelenamackatribes.resources.models.resources;

import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;

public class Food extends Resource {

  public Food(Resource resource) {
    super(resource.getId(), ResourceType.FOOD, resource.getAmount(), resource.getKingdom(),
        resource.getGeneration(), resource.getUpdatedAt());
  }
}
