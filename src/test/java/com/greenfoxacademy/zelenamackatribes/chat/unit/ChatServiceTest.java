package com.greenfoxacademy.zelenamackatribes.chat.unit;

import static org.mockito.ArgumentMatchers.any;

import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import com.greenfoxacademy.zelenamackatribes.chat.repositories.ChatRepository;
import com.greenfoxacademy.zelenamackatribes.chat.services.ChatService;
import com.greenfoxacademy.zelenamackatribes.chat.services.ChatServiceImpl;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

@TestMethodOrder(OrderAnnotation.class)
public class ChatServiceTest {

  private ChatService chatService;
  private ChatRepository chatRepository;
  private UserEntity dummyUser;

  @BeforeEach
  public void init() {
    dummyUser = new UserEntity();
    dummyUser.setUsername("Testovic Novak");
    chatRepository = Mockito.mock(ChatRepository.class);
    chatService = new ChatServiceImpl(chatRepository);
  }

  @Test
  @Order(1)
  public void getMessages() {
    List<ChatMessage> chatMessages = new ArrayList<>();
    ChatMessage msg1 = new ChatMessage("newer message", dummyUser);
    msg1.setCreatedAt(LocalDateTime.now().minusHours(12));
    ChatMessage msg2 = new ChatMessage("older message", dummyUser);
    msg2.setCreatedAt(LocalDateTime.now().minusHours(16));
    chatMessages.add(msg2);
    chatMessages.add(msg1);

    Mockito
        .when(chatRepository.findByCreatedAtGreaterThan(any()))
        .thenReturn(chatMessages);

    Assertions.assertEquals(chatService.getMessages(), chatMessages);
  }
}
