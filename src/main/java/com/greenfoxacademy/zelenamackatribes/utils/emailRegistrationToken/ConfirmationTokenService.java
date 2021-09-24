package com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidConfirmationTokenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.SendValidationEmailException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.utils.other.DefaultVals;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

  private final ConfirmationTokenRepository confirmationTokenRepository;

  public void saveConfirmationToken(ConfirmationToken token) {
    confirmationTokenRepository.save(token);
  }

  public ConfirmationToken getToken(String token) throws InvalidConfirmationTokenException {
    return confirmationTokenRepository.findByToken(token)
        .orElseThrow(() -> new InvalidConfirmationTokenException("Token not found"));
  }

  public int setConfirmedAt(String token) {
    return confirmationTokenRepository.updateConfirmedAt(
        token, LocalDateTime.now());
  }

  public String createConfirmationTokenAndSendMail(UserEntity newUser)
      throws MessagingException, SendValidationEmailException, IncorrectDefaultValueTypeException,
      DefaultValueNotFoundException, DefaultValuesFileMissingException {
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
        LocalDateTime.now()
            .plusMinutes(DefaultVals.getInt("timeValidity.defaultValidityConfirmToken")), newUser);
    saveConfirmationToken(confirmationToken);
    String link = System.getenv("CONF_TOKEN_LINK") + token;
    newUser.setConfirmationToken(confirmationToken);
    SendEmail
        .sendMail(newUser.getUsername(), newUser.getKingdom().getKingdomName(), newUser.getEmail(),
            link);
    return token;
  }

  public void deleteConfirmationToken(ConfirmationToken token) {
    confirmationTokenRepository.delete(token);
  }
}
