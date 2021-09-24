package com.greenfoxacademy.zelenamackatribes.resources.controllers;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.dtos.ResourceListResponseDTO;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

  private static Logger logger = LogManager.getLogger(ResourceController.class);
  private final JwtService jwtService;
  private final KingdomService kingdomService;
  private final ResourceService resourcesService;
  private final ModelMapper modelMapper;

  @Autowired
  public ResourceController(ModelMapper modelMapper, JwtService jwtService,
      KingdomService kingdomService, ResourceService resourcesService) {
    this.modelMapper = modelMapper;
    this.jwtService = jwtService;
    this.kingdomService = kingdomService;
    this.resourcesService = resourcesService;
  }

  @GetMapping("/kingdom/resources")
  public ResponseEntity<ResourceListResponseDTO> getResources(
      @RequestHeader(value = "Authorization", required = false) String token)
      throws KingdomNotFoundException, InvalidNumberOfResourceObjectsException,
      InvalidTokenException,
      ResourceNotFoundException {
    String kingdomName = jwtService.extractKingdomName(token);
    Kingdom kingdom = kingdomService.getByName(kingdomName);
    List<Resource> resources = resourcesService.getResources(kingdom);
    logger.info(
        "GET /kingdom/resources " + HttpStatus.OK + " " + kingdomName);

    return ResponseEntity.ok(new ResourceListResponseDTO(resources, modelMapper));
  }
}
