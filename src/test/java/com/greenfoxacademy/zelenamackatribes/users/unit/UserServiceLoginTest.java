package com.greenfoxacademy.zelenamackatribes.users.unit;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidUserCredentialsException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.users.services.UserService;
import com.greenfoxacademy.zelenamackatribes.users.services.UserServiceImpl;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationToken;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationTokenService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestMethodOrder(OrderAnnotation.class)
public class UserServiceLoginTest {

  private UserService userService;
  private UserRepository userRepository;
  private JwtService jwtService;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private KingdomService kingdomService;

  @MockBean
  private ConfirmationTokenService confirmationTokenService;

  @BeforeEach
  public void init() {
    jwtService = Mockito.mock(JwtService.class);
    userRepository = Mockito.mock(UserRepository.class);
    bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    kingdomService = Mockito.mock(KingdomService.class);
    confirmationTokenService = Mockito.mock(ConfirmationTokenService.class);
    userService = new UserServiceImpl(userRepository, jwtService, bCryptPasswordEncoder,
        kingdomService, confirmationTokenService);
  }

  @Test
  @Order(1)
  public void ifUserNotFoundShouldThrowInvalidUserCredentials() {
    Exception exception = Assertions.assertThrows(
        InvalidUserCredentialsException.class,
        () -> userService.login("Non Existus", "iAmNotHere")
    );
    Assertions.assertEquals("Username or password is incorrect.", exception.getMessage());
  }

  @Test
  @Order(2)
  public void ifWrongPasswordShouldThrowInvalidUserCredentials() throws Exception {
    UserEntity luckyUser = new UserEntity();
    luckyUser.setUsername("Hang Over");
    luckyUser.setPassword("RumVodkaZelena");
    luckyUser.setIsEmailConfirmed(true);
    ConfirmationToken confirmationToken = new ConfirmationToken();
    confirmationToken.setToken("8e5ac825-6659-43c1-86e7-f6fc9a08630d");

    confirmationToken.setUser(luckyUser);
    luckyUser.setConfirmationToken(confirmationToken);

    Mockito
        .when(confirmationTokenService.createConfirmationTokenAndSendMail(luckyUser))
        .thenReturn("8e5ac825-6659-43c1-86e7-f6fc9a08630d");
    Mockito
        .when(userRepository.findByUsername("Hang Over"))
        .thenReturn(Optional.of(luckyUser));
    Mockito
        .when(kingdomService.getKingdomForUser(luckyUser))
        .thenReturn(new Kingdom());

    Exception exception = Assertions.assertThrows(
        InvalidUserCredentialsException.class,
        () -> userService.login("Hang Over", "iCannotRememberWholeNight")
    );
    Assertions.assertEquals("Username or password is incorrect.", exception.getMessage());
  }

  @Test
  @Order(3)
  public void loginOK() throws Exception {
    UserEntity user = new UserEntity();
    user.setUsername("Pass Testovic");
    user.setPassword("encodedPassword");
    user.setIsEmailConfirmed(true);

    Mockito
        .when(jwtService.generate(user))
        .thenReturn("Dummy JWT string");
    Mockito
        .when(userRepository.findByUsername("Pass Testovic"))
        .thenReturn(Optional.of(user));
    Mockito
        .when(bCryptPasswordEncoder.encode("OKOKOK"))
        .thenReturn("encodedPassword");
    Mockito
        .when(bCryptPasswordEncoder.matches("OKOKOK", "encodedPassword"))
        .thenReturn(true);
    Mockito
        .when(kingdomService.getKingdomForUser(user))
        .thenReturn(new Kingdom(null, "testkingdom", user,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), null));

    String loggedUserToken = "";
    loggedUserToken = userService.login("Pass Testovic", "OKOKOK");

    Assertions.assertEquals("Dummy JWT string", loggedUserToken);
  }
}
