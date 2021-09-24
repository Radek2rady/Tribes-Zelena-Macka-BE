package com.greenfoxacademy.zelenamackatribes.troops.controllers;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidAcademyException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingIdException;
import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidOwnerException;
import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.dtos.TroopCreateRequestDTO;
import com.greenfoxacademy.zelenamackatribes.troops.dtos.TroopCreateResponseDTO;
import com.greenfoxacademy.zelenamackatribes.troops.dtos.TroopDTO;
import com.greenfoxacademy.zelenamackatribes.troops.dtos.TroopListResponseDTO;
import com.greenfoxacademy.zelenamackatribes.troops.dtos.TroopUpgradeRequestDTO;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.InvalidTroopUpgradeException;
import com.greenfoxacademy.zelenamackatribes.troops.exceptions.TroopNotFoundException;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.util.InputMismatchException;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TroopController {

  private static Logger logger = LogManager.getLogger(TroopController.class);

  private final TroopService troopService;
  private final BuildingService buildingService;
  private final JwtService jwtService;
  private final KingdomService kingdomService;
  private final ModelMapper modelMapper;

  @Autowired
  public TroopController(TroopService troopService, BuildingService buildingService,
      JwtService jwtService, KingdomService kingdomService, ModelMapper modelMapper) {
    this.troopService = troopService;
    this.buildingService = buildingService;
    this.jwtService = jwtService;
    this.kingdomService = kingdomService;
    this.modelMapper = modelMapper;
  }

  @PostMapping("/kingdom/troops")
  public ResponseEntity<TroopCreateResponseDTO> createTroop(
      @RequestHeader(value = "Authorization", required = false) String token,
      @Valid @RequestBody TroopCreateRequestDTO troopCreateRequest)
      throws InvalidTokenException, InvalidBuildingIdException, InvalidOwnerException,
      InvalidAcademyException, NotEnoughResourcesException, InvalidNumberOfResourceObjectsException,
      ResourceNotFoundException, InputMismatchException, DefaultValuesFileException {
    String kingdomName = jwtService.extractKingdomName(token);
    Building academy = buildingService.getBuildingById(troopCreateRequest.getBuildingId());
    if (!(academy.getKingdom().getKingdomName().equals(kingdomName))) {
      logger
          .error("POST /kingdom/troops " + academy.getKingdom()
              .getKingdomName() + " not belong to " + kingdomName);
      throw new InvalidOwnerException("Forbidden action");
    }
    Troop troop = troopService.createTroop(academy);
    logger.info("POST /kingdom/troops " + HttpStatus.OK + " " + troop.getId());
    return ResponseEntity
        .ok()
        .body(modelMapper.map(troop, TroopCreateResponseDTO.class));
  }

  @GetMapping("kingdom/troops")
  public ResponseEntity<TroopListResponseDTO> getTroops(
      @RequestHeader(value = "Authorization", required = false) String token)
      throws InvalidTokenException, KingdomNotFoundException {
    String kingdomName = jwtService.extractKingdomName(token);
    Long kingdomId = kingdomService.getByName(kingdomName).getId();
    logger.info("GET /kingdom/troops " + HttpStatus.OK + " " + kingdomId);
    return ResponseEntity
        .ok()
        .body(new TroopListResponseDTO(troopService.getTroopsByKingdomId(kingdomId)));
  }

  @GetMapping("kingdom/troops/{troop_id}")
  public ResponseEntity<TroopCreateResponseDTO> getTroopById(
      @RequestHeader(value = "Authorization", required = false) String token,
      @PathVariable("troop_id") int troopId)
      throws InvalidTokenException, KingdomNotFoundException,
      TroopNotFoundException, ForbiddenActionException {
    String kingdomName = jwtService.extractKingdomName(token);
    Long kingdomId = kingdomService.getByName(kingdomName).getId();
    var createdTroop = troopService.getTroop(troopId, kingdomId);
    logger.info("GET kingdom/troops/{troop_id} " + HttpStatus.OK + " " + troopId);
    return ResponseEntity
        .ok()
        .body(modelMapper.map(createdTroop, TroopCreateResponseDTO.class));
  }

  @PutMapping("kingdom/troops/{troop_id}")
  public ResponseEntity<TroopDTO> upgradeTroop(
      @RequestHeader(value = "Authorization") String token,
      @PathVariable("troop_id") Long troopId,
      @Valid @RequestBody TroopUpgradeRequestDTO troopUpgradeRequest)
      throws KingdomNotFoundException, InvalidBuildingIdException, ForbiddenActionException,
      TroopNotFoundException, InvalidTroopUpgradeException, InvalidAcademyException,
      NotEnoughResourcesException, ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException, DefaultValuesFileException {
    var kingdomName = jwtService.extractKingdomName(token);
    var kingdom = kingdomService.getByName(kingdomName);
    var academy = buildingService.getBuildingById(troopUpgradeRequest.getBuildingId());
    var troop = troopService.getTroop(troopId, kingdom.getId());
    var upgradedTroop = troopService.upgrade(kingdom, troop, academy);
    logger.info("PUT /kingdom/troops/" + troopId + " " + HttpStatus.OK + " " + upgradedTroop);
    return ResponseEntity.ok(modelMapper.map(upgradedTroop, TroopDTO.class));
  }
}
