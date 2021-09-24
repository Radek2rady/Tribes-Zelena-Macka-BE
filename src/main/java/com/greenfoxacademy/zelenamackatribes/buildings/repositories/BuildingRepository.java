package com.greenfoxacademy.zelenamackatribes.buildings.repositories;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

  List<Building> findAllByKingdom(Kingdom kingdom);

  @Transactional
  void deleteAllByKingdom(Kingdom kingdom);
}
