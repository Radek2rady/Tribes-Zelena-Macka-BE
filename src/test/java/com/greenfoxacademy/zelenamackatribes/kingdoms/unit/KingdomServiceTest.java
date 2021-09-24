package com.greenfoxacademy.zelenamackatribes.kingdoms.unit;

import static org.mockito.ArgumentMatchers.any;

import com.greenfoxacademy.zelenamackatribes.buildings.services.BuildingService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.repositories.KingdomScoreRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomServiceImpl;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.LeaderboardService;
import com.greenfoxacademy.zelenamackatribes.resources.services.ResourceService;
import com.greenfoxacademy.zelenamackatribes.troops.services.TroopService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class KingdomServiceTest {

  private KingdomService kingdomService;
  private KingdomScoreRepository kingdomScoreRepository;

  @Mock
  private KingdomRepository kingdomRepository;

  @Mock
  private ResourceService resourceService;

  @Mock
  private BuildingService buildingService;

  @Mock
  private TroopService troopService;

  @Mock
  private LeaderboardService leaderboardService;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    kingdomService = new KingdomServiceImpl(buildingService, kingdomRepository,
        kingdomScoreRepository, resourceService, troopService, leaderboardService);
  }

  @Test
  public void findKingdomByIdOk() throws KingdomNotFoundException {
    //arrange
    Kingdom fakeKingdom = new Kingdom(1L, "Amandica", null, Collections.emptyList(),
        Collections.emptyList(), Collections.emptyList(), null);
    kingdomRepository.save(fakeKingdom);
    //act
    Mockito.when(kingdomRepository.findById(1L)).thenReturn(Optional.of(fakeKingdom));
    var result = kingdomService.getById(1L);
    //assert
    Assertions.assertEquals(fakeKingdom.getId(), result.getId());
  }

  @Test
  public void findKingdomByIdNotFound() {
    Mockito.when(kingdomRepository.findById(any())).thenReturn(Optional.empty());
    KingdomNotFoundException exception = Assertions.assertThrows(KingdomNotFoundException.class,
        () -> kingdomService.getById(150L));
    Assertions.assertEquals("Kingdom id not found", exception.getMessage());
  }
}
