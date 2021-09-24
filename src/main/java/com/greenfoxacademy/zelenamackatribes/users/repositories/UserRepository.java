package com.greenfoxacademy.zelenamackatribes.users.repositories;

import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(String name);

  Optional<UserEntity> findByEmail(String email);
}
