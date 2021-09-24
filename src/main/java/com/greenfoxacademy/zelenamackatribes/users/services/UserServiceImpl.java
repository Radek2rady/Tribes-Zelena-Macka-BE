package com.greenfoxacademy.zelenamackatribes.users.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.InvalidNumberOfResourceObjectsException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.NotEnoughResourcesException;
import com.greenfoxacademy.zelenamackatribes.resources.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailAlreadyConfirmed;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailNotConfirmedException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailTakenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidConfirmationTokenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidUserCredentialsException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.KingdomNameTakenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.SendValidationEmailException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.UsernameTakenException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationTokenService;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.SendEmail;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.time.LocalDateTime;
import java.util.Optional;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final KingdomService kingdomService;
  private final ConfirmationTokenService confirmationTokenService;

  @Autowired
  public UserServiceImpl(
      UserRepository userRepository, JwtService jwtService,
      BCryptPasswordEncoder bCryptPasswordEncoder, KingdomService kingdomService,
      ConfirmationTokenService confirmationTokenService) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.kingdomService = kingdomService;
    this.confirmationTokenService = confirmationTokenService;
  }

  @Override
  public UserEntity getByUsername(String username) {
    return userRepository.findByUsername(username).orElse(null);
  }

  @Override
  public UserEntity createNewUser(String username, String password,
      String email, String kingdomName)
      throws UsernameTakenException, DefaultValuesFileException, MessagingException,
      SendValidationEmailException, KingdomNameTakenException, EmailTakenException,
      InvalidBuildingTypeException, NotEnoughResourcesException, ResourceNotFoundException,
      InvalidNumberOfResourceObjectsException {
    if (checkExistingUser(username)) {
      throw new UsernameTakenException("Username is already taken.");
    } else if (checkExistingUserKingdomName(kingdomName)) {
      throw new KingdomNameTakenException("Kingdom name is already taken.");
    } else if (checkExistingUserEmail(email)) {
      throw new EmailTakenException("Email is already taken.");
    } else {
      UserEntity newUser = new UserEntity();
      newUser.setUsername(username);
      newUser.setPassword(bCryptPasswordEncoder.encode(password));
      newUser.setEmail(email);
      userRepository.save(newUser);
      Kingdom newKingdom = kingdomService.createKingdom(newUser, kingdomName);
      newUser.setKingdom(newKingdom);
      confirmationTokenService.createConfirmationTokenAndSendMail(newUser);
      return newUser;
    }
  }

  @Override
  public String login(String username, String password)
      throws InvalidUserCredentialsException, KingdomNotFoundException,
      EmailNotConfirmedException {
    Optional<UserEntity> userOptional = userRepository.findByUsername(username);
    if (userOptional.isPresent()) {
      UserEntity user = userOptional.get();
      if (!user.getIsEmailConfirmed()) {
        throw new EmailNotConfirmedException("Email is not confirmed");
      }
      if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
        user.setKingdom(kingdomService.getKingdomForUser(user));
        return jwtService.generate(user);
      }
    }
    throw new InvalidUserCredentialsException();
  }

  @Override
  public void confirmToken(String token)
      throws InvalidConfirmationTokenException, EmailAlreadyConfirmed,
      MessagingException, SendValidationEmailException, IncorrectDefaultValueTypeException,
      DefaultValueNotFoundException, DefaultValuesFileMissingException {
    var confirmationToken = confirmationTokenService.getToken(token);
    validateToken(token);
    var userEntity = userRepository.findByUsername(confirmationToken.getUser().getUsername())
        .orElseThrow(() -> new InvalidConfirmationTokenException("Not valid token"));
    userEntity.setIsEmailConfirmed(true);
    confirmationTokenService.setConfirmedAt(token);
    userRepository.save(userEntity);
  }

  private boolean validateToken(String token)
      throws InvalidConfirmationTokenException, EmailAlreadyConfirmed, MessagingException,
      SendValidationEmailException, IncorrectDefaultValueTypeException,
      DefaultValueNotFoundException, DefaultValuesFileMissingException {
    var confirmationToken = confirmationTokenService.getToken(token);
    LocalDateTime expiredAt = confirmationToken.getExpiresAt();

    if (expiredAt
        .isBefore(LocalDateTime.now()
            .minusMinutes(DefaultVals.getInt("timeZone.timeZoneChangeMinutes")))) {
      confirmationTokenService.deleteConfirmationToken(confirmationToken);
      sendNewConfirmationToken(token);
      return false;
    }
    if (checkEmailConfirmed(token)) {
      throw new EmailAlreadyConfirmed("Email is already confirmed");
    }
    return true;
  }

  private boolean checkExistingUser(String username) {
    return userRepository.findByUsername(username).isPresent();
  }

  private boolean checkExistingUserKingdomName(String kingdomName) {
    return kingdomService.getKingdomByNameFromString(kingdomName);
  }

  private boolean checkExistingUserEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }

  private void sendNewConfirmationToken(String token)
      throws InvalidConfirmationTokenException, MessagingException,
      SendValidationEmailException, IncorrectDefaultValueTypeException,
      DefaultValueNotFoundException, DefaultValuesFileMissingException {
    var userEntity = confirmationTokenService.getToken(token).getUser();
    String newToken = confirmationTokenService.createConfirmationTokenAndSendMail(userEntity);
    String link = System.getenv("CONF_TOKEN_LINK") + newToken;
    SendEmail.sendMail(getByUsername(userEntity.getUsername()).getUsername(),
        getByUsername(userEntity.getUsername()).getKingdom().getKingdomName(),
        getByUsername(userEntity.getUsername()).getEmail(), link);
  }

  private boolean checkEmailConfirmed(String token) throws InvalidConfirmationTokenException {
    return confirmationTokenService.getToken(token).getUser().getIsEmailConfirmed();
  }
}
