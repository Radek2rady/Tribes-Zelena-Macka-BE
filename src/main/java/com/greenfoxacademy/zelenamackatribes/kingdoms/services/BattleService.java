package com.greenfoxacademy.zelenamackatribes.kingdoms.services;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.ForbiddenActionException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.KingdomsStatsResponseDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomHasNoTownhallException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomOutOfRangeException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;

public interface BattleService {

  KingdomsStatsResponseDTO battle(Long playerKingdomId, Long enemyKingdomId)
      throws KingdomNotFoundException, ForbiddenActionException, KingdomHasNoTownhallException,
      ResourceNotFoundException, KingdomOutOfRangeException,
      InvalidNumberOfResourceObjectsException, DefaultValuesFileException;
}
