package com.greenfoxacademy.zelenamackatribes.troops.repositories;

import com.greenfoxacademy.zelenamackatribes.buildings.models.Building;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.troops.models.Troop;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TroopRepository extends JpaRepository<Troop, Long> {

  @Query("select t from Troop t where t.kingdom.id = ?1")
  List<Troop> findAllByKingdomId(Long kingdomId);

  List<Troop> findAllByKingdom(Kingdom kingdom);

  long countAllByAcademyAndFinishedAtGreaterThan(Building academy, Long finishedAt);

  @Transactional
  void deleteAllByKingdom(Kingdom kingdom);
}
