package com.greenfoxacademy.zelenamackatribes.resources.repositories;

import com.greenfoxacademy.zelenamackatribes.resources.models.Resource;
import com.greenfoxacademy.zelenamackatribes.resources.models.ResourceGenerationChange;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ResourceGenerationChangeRepository extends
    JpaRepository<ResourceGenerationChange, Long> {

  List<ResourceGenerationChange> findAllByResourceInAndChangeAtIsLessThanEqual(
      List<Resource> resources, Long changeAt);

  @Transactional
  void deleteAllByResourceIn(List<Resource> resources);
}
