package com.greenfoxacademy.zelenamackatribes.users.services;

import com.greenfoxacademy.zelenamackatribes.buildings.exceptions.InvalidBuildingTypeException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.DefaultValuesFileException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
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
import javax.mail.MessagingException;

public interface UserService {

  UserEntity getByUsername(String username);

  UserEntity createNewUser(String username, String password, String email, String kingdomName)
      throws UsernameTakenException, DefaultValuesFileException, KingdomNameTakenException,
      EmailTakenException, InvalidConfirmationTokenException, MessagingException,
      SendValidationEmailException, InvalidBuildingTypeException, NotEnoughResourcesException,
      ResourceNotFoundException, InvalidNumberOfResourceObjectsException;

  String login(String username, String password)
      throws InvalidUserCredentialsException, KingdomNotFoundException, EmailNotConfirmedException;

  void confirmToken(String token)
      throws InvalidConfirmationTokenException, EmailAlreadyConfirmed,
      MessagingException, SendValidationEmailException, IncorrectDefaultValueTypeException,
      DefaultValueNotFoundException, DefaultValuesFileMissingException;

}
