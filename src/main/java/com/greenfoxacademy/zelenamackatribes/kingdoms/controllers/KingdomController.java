package com.greenfoxacademy.zelenamackatribes.kingdoms.controllers;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomsStatsResponseDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.LeaderboardPageDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.LeaderboardResponseDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.IncorrectPageParameterException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomHasNoTownhallException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomOutOfRangeException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.BattleService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kingdom")
public class KingdomController {

  private static Logger logger = LogManager.getLogger(KingdomController.class);
  private final KingdomService kingdomService;
  private final LeaderboardService leaderboardService;
  private final JwtService jwtService;
  private final ModelMapper modelMapper;
  private final BattleService battleService;

  @Autowired
  public KingdomController(KingdomService kingdomService, LeaderboardService leaderboardService,
      JwtService jwtService, ModelMapper modelMapper, BattleService battleService) {
    this.kingdomService = kingdomService;
    this.leaderboardService = leaderboardService;
    this.jwtService = jwtService;
    this.modelMapper = modelMapper;
    this.battleService = battleService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<KingdomDTO> getKingdomById(
      @PathVariable("id") Long kingdomId)
      throws KingdomNotFoundException {
    return ResponseEntity
        .ok()
        .body(modelMapper.map(kingdomService.getById(kingdomId), KingdomDTO.class));
  }

  @PostMapping("/fight/{id}")
  public ResponseEntity<KingdomsStatsResponseDTO> fight(@PathVariable("id") Long enemyKingdomId,
      @RequestHeader(value = "Authorization") String token)
      throws KingdomNotFoundException, ForbiddenActionException, KingdomHasNoTownhallException,
      ResourceNotFoundException, KingdomOutOfRangeException,
      InvalidNumberOfResourceObjectsException, DefaultValuesFileException {
    long kingdomId = jwtService.parse(token).getKingdomId();
    KingdomsStatsResponseDTO response = battleService.battle(kingdomId, enemyKingdomId);
    logger.info("POST /kingdom/fight" + enemyKingdomId + " " + HttpStatus.OK + " " + response);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/leaderboard")
  public ResponseEntity<LeaderboardResponseDTO> leaderboard(
      @RequestParam int pageNo,
      @RequestParam int pageSize,
      @RequestParam String scoreType,
      @RequestParam boolean isHistory)
      throws IncorrectPageParameterException, InvalidNumberOfResourceObjectsException,
      ResourceNotFoundException {
    LeaderboardPageDTO page = leaderboardService.createLeaderboard(
        pageNo - 1, pageSize, scoreType, isHistory);
    LeaderboardResponseDTO response = modelMapper.map(page, LeaderboardResponseDTO.class);
    logger.info("GET /kingdom/leaderboard " + HttpStatus.OK + " " + response);
    return ResponseEntity.ok().body(response);
  }
}
