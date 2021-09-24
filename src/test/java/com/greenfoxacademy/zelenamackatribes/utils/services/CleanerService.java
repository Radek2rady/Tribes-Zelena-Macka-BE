package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.buildings.repositories.BuildingRepository;
import com.greenfoxacademy.zelenamackatribes.chat.repositories.ChatRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomScoreRepository;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceGenerationChangeRepository;
import com.greenfoxacademy.zelenamackatribes.resources.repositories.ResourceRepository;
import com.greenfoxacademy.zelenamackatribes.troops.repositories.TroopRepository;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CleanerService {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private KingdomRepository kingdomRepository;
  @Autowired
  private ResourceRepository resourceRepository;
  @Autowired
  private ResourceGenerationChangeRepository resourceGenerationChangeRepository;
  @Autowired
  private TroopRepository troopRepository;
  @Autowired
  private BuildingRepository buildingRepository;
  @Autowired
  private KingdomScoreRepository kingdomScoreRepository;
  @Autowired
  private ChatRepository chatRepository;
  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;


  public void cleanDatabase() {
    resourceGenerationChangeRepository.deleteAll();
    resourceRepository.deleteAll();
    troopRepository.deleteAll();
    buildingRepository.deleteAll();
    kingdomScoreRepository.deleteAll();
    kingdomRepository.deleteAll();
    chatRepository.deleteAll();
    confirmationTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

}
