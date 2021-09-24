package com.greenfoxacademy.zelenamackatribes.chat.models;

import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  private UserEntity user;

  private String message;
  private LocalDateTime createdAt;

  public ChatMessage(String message, UserEntity user, LocalDateTime createdAt) {
    this.message = message;
    this.user = user;
    this.createdAt = createdAt;
  }

  public ChatMessage(String message, UserEntity user) {
    this(message, user, LocalDateTime.now());
  }
}
