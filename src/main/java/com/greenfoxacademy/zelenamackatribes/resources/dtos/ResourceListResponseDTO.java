package com.greenfoxacademy.zelenamackatribes.resources.dtos;

import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.modelmapper.ModelMapper;

@Data
public class ResourceListResponseDTO {

  private List<ResourceDTO> resources;

  public ResourceListResponseDTO(List<Resource> resources, ModelMapper modelMapper) {
    List<ResourceDTO> resourcesDTO = new ArrayList<>();
    for (Resource resource : resources) {
      resourcesDTO.add(modelMapper.map(resource, ResourceDTO.class));
    }
    this.resources = resourcesDTO;
  }
}
