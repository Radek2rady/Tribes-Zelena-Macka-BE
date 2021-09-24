package com.greenfoxacademy.zelenamackatribes.buildings.dtos;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildingListResponseDTO {

  private List<BuildingDTO> buildings;

}
