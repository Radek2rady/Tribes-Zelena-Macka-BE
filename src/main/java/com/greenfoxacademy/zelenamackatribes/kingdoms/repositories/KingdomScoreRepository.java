package com.greenfoxacademy.zelenamackatribes.kingdoms.repositories;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.KingdomScore;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface KingdomScoreRepository extends JpaRepository<KingdomScore, Long> {

  Optional<KingdomScore> findByKingdom(Kingdom kingdom);

  @Transactional
  void deleteByKingdom(Kingdom kingdom);

  KingdomScore findByKingdomAndIsHistory(Kingdom kingdom, boolean isHistory);

  Page<KingdomScore> findByIsHistory(boolean isHistory, Pageable pageable);

}
