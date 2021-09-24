package com.greenfoxacademy.zelenamackatribes.resources.dtos;

import lombok.Data;

@Data
public class ResourceDTO {

  private String type;
  private int amount;
  private int generation;
  private long updatedAt;
}
