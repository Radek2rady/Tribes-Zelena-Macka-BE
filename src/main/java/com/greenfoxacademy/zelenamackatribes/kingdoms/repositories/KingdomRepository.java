package com.greenfoxacademy.zelenamackatribes.kingdoms.repositories;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KingdomRepository extends JpaRepository<Kingdom, Long> {

  Optional<Kingdom> findKingdomByKingdomName(String kingdomName);

  Optional<Kingdom> findKingdomByUser(UserEntity user);
}
