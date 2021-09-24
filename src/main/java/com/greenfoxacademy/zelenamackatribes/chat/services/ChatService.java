package com.greenfoxacademy.zelenamackatribes.chat.services;

import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import java.util.List;

public interface ChatService {

  List<ChatMessage> getMessages();

  void postMessage(String message, long userId);
}
