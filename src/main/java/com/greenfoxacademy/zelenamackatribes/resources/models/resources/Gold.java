package com.greenfoxacademy.zelenamackatribes.resources.models.resources;

import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceType;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Gold extends Resource {

  public Gold(Resource resource) {
    super(resource.getId(), ResourceType.GOLD, resource.getAmount(), resource.getKingdom(),
        resource.getGeneration(), resource.getUpdatedAt());
  }
}
