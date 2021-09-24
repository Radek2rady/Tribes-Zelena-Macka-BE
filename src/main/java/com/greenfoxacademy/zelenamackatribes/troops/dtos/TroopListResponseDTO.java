package com.greenfoxacademy.zelenamackatribes.troops.dtos;

import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TroopListResponseDTO {

  private List<Troop> troops;

}
