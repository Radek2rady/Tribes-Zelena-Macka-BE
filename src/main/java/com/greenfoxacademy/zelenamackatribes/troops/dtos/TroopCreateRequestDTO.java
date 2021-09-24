package com.greenfoxacademy.zelenamackatribes.troops.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TroopCreateRequestDTO {

  @NotNull(message = "Building id is required")
  private Long buildingId;

}
