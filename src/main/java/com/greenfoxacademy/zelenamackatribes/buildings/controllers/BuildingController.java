package com.greenfoxacademy.zelenamackatribes.buildings.controllers;

import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingCreateRequestDTO;
import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingDTO;
import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingListResponseDTO;
import com.greenfoxacademy.zelenamackatribes.buildings.dtos.BuildingUpgradeResponseDTO;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.BuildingIdNotBelongToKingdomException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingUpgradeException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MaximumLevelReachedException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.MissingParamException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import io.jsonwebtoken.JwtException;
import java.util.InputMismatchException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kingdom")
public class BuildingController {

  private static Logger logger = LogManager.getLogger(BuildingController.class);
  private final BuildingService buildingService;
  private final KingdomService kingdomService;
  private final JwtService jwtService;
  private final ModelMapper modelMapper;

  public BuildingController(BuildingService buildingService, KingdomService kingdomService,
      JwtService jwtService, ModelMapper modelMapper) {
    this.buildingService = buildingService;
    this.kingdomService = kingdomService;
    this.jwtService = jwtService;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/buildings")
  public ResponseEntity<BuildingListResponseDTO> getBuildings(
      @RequestHeader(value = "Authorization") String token)
      throws InvalidTokenException, KingdomNotFoundException {
    var kingdomName = jwtService.extractKingdomName(token);
    var kingdom = kingdomService.getByName(kingdomName);
    var kingdomsBuildings = buildingService.getBuildingsForKingdom(kingdom);
    logger.info("GET /kingdom/buildings " + HttpStatus.OK + " " + kingdomName);
    return ResponseEntity
        .ok()
        .body(modelMapper.map(kingdomsBuildings, BuildingListResponseDTO.class));
  }

  @GetMapping("/buildings/{building_id}")
  public ResponseEntity<BuildingDTO> getBuilding(
      @RequestHeader(value = "Authorization") String token,
      @PathVariable("building_id") Long buildingId)
      throws InvalidTokenException, KingdomNotFoundException, InvalidBuildingIdException,
      ForbiddenActionException {
    var building = buildingService.getBuildingForKingdom(buildingId,
        kingdomService.getByName(jwtService.extractKingdomName(token)));
    logger.info("GET /kingdom/buildings/" + buildingId + " " + HttpStatus.OK + " " + building);
    return ResponseEntity
        .ok()
        .body(modelMapper.map(building, BuildingDTO.class));
  }

  @PostMapping("/buildings")
  public ResponseEntity<BuildingDTO> builtNewBuilding(
      @RequestHeader(value = "Authorization", required = false) String token,
      @RequestBody(required = false) BuildingCreateRequestDTO buildingDto)
      throws MissingParamException, InvalidBuildingTypeException, KingdomNotFoundException,
      JwtException, InvalidNumberOfResourceObjectsException, ResourceNotFoundException,
      NotEnoughResourcesException, InputMismatchException, DefaultValuesFileException {

    String kingdomName = jwtService.extractKingdomName(token);
    if (buildingDto == null || buildingDto.getType() == null) {
      logger.error(
          "POST /kingdom/buildings " + buildingDto + " or type is null."
              + " Missing parameter(s): type!");
      throw new MissingParamException("Missing parameter(s): type!");
    }
    Building createdBuilding = buildingService
        .createBuilding(buildingDto.getType(), kingdomService.getByName(kingdomName));
    logger.info("POST /kingdom/buildings " + HttpStatus.OK + " " + createdBuilding);
    return ResponseEntity
        .ok(modelMapper.map(createdBuilding, BuildingDTO.class));
  }

  @PutMapping("/buildings/{building_id}")
  public ResponseEntity<BuildingUpgradeResponseDTO> upgradeBuilding(
      @RequestHeader(value = "Authorization") String token,
      @PathVariable("building_id") Long buildingId)
      throws KingdomNotFoundException, InvalidBuildingTypeException,
      NotEnoughResourcesException, InvalidBuildingIdException,
      MaximumLevelReachedException, InvalidBuildingUpgradeException,
      BuildingIdNotBelongToKingdomException,
      ResourceNotFoundException, InvalidNumberOfResourceObjectsException,
      DefaultValuesFileException {
    String kingdomName = jwtService.extractKingdomName(token);
    Kingdom kingdom = kingdomService.getByName(kingdomName);
    Building upgradedBuilding = buildingService.upgradeBuilding(kingdom, buildingId);
    logger.info(
        "PUT /kingdom/buildings/" + buildingId + " " + HttpStatus.OK + " " + upgradedBuilding);
    return ResponseEntity.ok(modelMapper.map(upgradedBuilding, BuildingUpgradeResponseDTO.class));
  }
}
