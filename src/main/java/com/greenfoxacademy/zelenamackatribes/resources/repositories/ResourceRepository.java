package com.greenfoxacademy.zelenamackatribes.resources.repositories;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

  List<Resource> findResourcesByKingdom(Kingdom kingdom);

  @Transactional
  void deleteAllByKingdom(Kingdom kingdom);
}
