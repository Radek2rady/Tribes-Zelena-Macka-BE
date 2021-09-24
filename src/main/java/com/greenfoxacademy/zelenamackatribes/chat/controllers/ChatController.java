package com.greenfoxacademy.zelenamackatribes.chat.controllers;

import com.greenfoxacademy.zelenamackatribes.chat.dtos.ChatMessageRequestDTO;
import com.greenfoxacademy.zelenamackatribes.chat.dtos.ChatMessageResponseDTO;
import com.greenfoxacademy.zelenamackatribes.chat.services.ChatService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import com.greenfoxacademy.zelenamackatribes.utils.services.ModelMapperService;
import java.util.List;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

  private static final Logger logger = LogManager.getLogger(ChatController.class);
  private ChatService chatService;
  private ModelMapperService modelMapperService;
  private JwtService jwtService;

  @Autowired
  public ChatController(ChatService chatService, ModelMapperService modelMapperService,
      JwtService jwtService) {
    this.chatService = chatService;
    this.modelMapperService = modelMapperService;
    this.jwtService = jwtService;
  }

  @PostMapping("/message")
  public ResponseEntity postMessage(
      @Valid @RequestBody ChatMessageRequestDTO dto,
      @RequestHeader(value = "Authorization") String token) {
    long userId = jwtService.parse(token).getId();
    chatService.postMessage(dto.getMessage(), userId);
    logger.info("POST /message - userId: " + userId + ", message: " + dto.getMessage());
    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("/messages")
  public ResponseEntity<List<ChatMessageResponseDTO>> getMessages() {
    logger.info("GET /messages");
    return ResponseEntity.ok(modelMapperService.mapAll(
        chatService.getMessages(), ChatMessageResponseDTO.class
    ));
  }
}
