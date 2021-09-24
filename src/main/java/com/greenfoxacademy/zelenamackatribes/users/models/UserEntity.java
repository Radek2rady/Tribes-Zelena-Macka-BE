package com.greenfoxacademy.zelenamackatribes.users.models;

import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationToken;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String username;

  private String password;

  @Column(name = "avatar")
  @ColumnDefault("'http://avatar.loc/my.png'")
  private String avatar;

  private final Integer points = 0;

  @NonNull
  @Column(name = "email")
  private String email;

  @OneToOne(mappedBy = "user")
  private Kingdom kingdom;

  @OneToMany(mappedBy = "user")
  private List<ChatMessage> chats;

  @OneToOne(mappedBy = "user")
  private ConfirmationToken confirmationToken;

  @Column(name = "is_confirmed_email")
  private Boolean isEmailConfirmed = false;

  public UserEntity(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
