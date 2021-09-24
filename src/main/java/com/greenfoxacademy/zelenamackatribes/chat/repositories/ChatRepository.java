package com.greenfoxacademy.zelenamackatribes.chat.repositories;

import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

  List<ChatMessage> findByCreatedAtGreaterThan(LocalDateTime localDateTime);
}
