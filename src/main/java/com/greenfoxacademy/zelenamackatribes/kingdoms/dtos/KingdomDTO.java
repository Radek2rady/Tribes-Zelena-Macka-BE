package com.greenfoxacademy.zelenamackatribes.kingdoms.dtos;

import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingDTO;
import com.greenfoxacademy.zelenamackatribes.resources.dtos.ResourceDTO;
import com.greenfoxacademy.zelenamackatribes.troops.dtos.TroopDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KingdomDTO {

  private Long id;
  private String name;
  private Long userId;
  private List<BuildingDTO> buildings;
  private List<ResourceDTO> resources;
  private List<TroopDTO> troops;
}
