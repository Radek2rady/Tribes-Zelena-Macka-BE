package com.greenfoxacademy.zelenamackatribes.chat.services;

import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import com.greenfoxacademy.zelenamackatribes.chat.repositories.ChatRepository;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

  @PersistenceContext
  private EntityManager entityManager;

  private ChatRepository chatRepository;

  @Autowired
  public ChatServiceImpl(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  @Override
  public List<ChatMessage> getMessages() {
    return chatRepository.findByCreatedAtGreaterThan(LocalDateTime.now().minusDays(1));
  }

  @Override
  public void postMessage(String text, long userId) {
    UserEntity user = entityManager.getReference(UserEntity.class, userId);
    ChatMessage chatMessage = new ChatMessage(text, user, LocalDateTime.now());
    chatRepository.save(chatMessage);
  }
}
