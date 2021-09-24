package com.greenfoxacademy.zelenamackatribes.resources.dtos;

import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourcesDTO {

  private List<Resource> resources;

}
