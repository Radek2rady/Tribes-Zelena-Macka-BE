package com.greenfoxacademy.zelenamackatribes.kingdoms.services;

import com.greenfoxacademy.zelenamackatribes.kingdoms.dtos.LeaderboardPageDTO;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.IncorrectPageParameterException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;

public interface LeaderboardService {
  LeaderboardPageDTO createLeaderboard(Integer pageNo, Integer pageSize, String sortBy,
      boolean isHistory)
      throws IncorrectPageParameterException, InvalidNumberOfResourceObjectsException,
      ResourceNotFoundException;

  void recountKingdomScores()
      throws InvalidNumberOfResourceObjectsException, ResourceNotFoundException;

  void updateScore(Kingdom kingdom)
      throws ResourceNotFoundException, InvalidNumberOfResourceObjectsException;
}
