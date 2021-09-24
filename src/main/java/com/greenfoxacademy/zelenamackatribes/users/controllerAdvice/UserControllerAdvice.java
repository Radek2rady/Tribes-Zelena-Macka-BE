package com.greenfoxacademy.zelenamackatribes.users.controllerAdvice;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import com.greenfoxacademy.zelenamackatribes.users.controllers.UserController;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailAlreadyConfirmed;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailNotConfirmedException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailTakenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidConfirmationTokenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidUserCredentialsException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.KingdomNameTakenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.UsernameTakenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadFilesizeException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadFiletypeException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadReadWriteException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice(basePackageClasses = {UserController.class})
public class UserControllerAdvice {

  private static Logger logger = LogManager.getLogger(UserControllerAdvice.class);

  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ErrorResponseDTO requestBodyMissing() {
    String bodyMalformed = "Request body malformed or missing.";
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST
        + " HttpMessageNotReadableException: " + bodyMalformed);
    return new ErrorResponseDTO(bodyMalformed);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorResponseDTO someFieldMissing(MethodArgumentNotValidException ex) {
    List<String> errorMessages = ex.getBindingResult().getAllErrors()
        .stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.toList());
    if (errorMessages.size() > 1
        && errorMessages.contains("Password is required.")
        && errorMessages.contains("Username is required.")) {
      String namePasswordRequired = "Username and password are required.";
      logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST
          + " MethodArgumentNotValidException: " + namePasswordRequired);
      return new ErrorResponseDTO(namePasswordRequired);
    }
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST + " " + errorMessages.get(0));
    return new ErrorResponseDTO(errorMessages.get(0));
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED) // 401
  @ExceptionHandler(InvalidUserCredentialsException.class)
  public ErrorResponseDTO invalidUser() {
    String namePasswordIncor = "Username or password is incorrect.";
    logger.error("HttpStatus: " + HttpStatus.UNAUTHORIZED
        + " InvalidUserCredentialsException: " + namePasswordIncor);
    return new ErrorResponseDTO(namePasswordIncor);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(UsernameTakenException.class)
  public ErrorResponseDTO usernameTaken() {
    String nameTaken = "Username is already taken.";
    logger.error("HttpStatus: " + HttpStatus.CONFLICT
        + " UsernameTakenException: " + nameTaken);
    return new ErrorResponseDTO(nameTaken);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(KingdomNameTakenException.class)
  public ErrorResponseDTO kingdomnameTaken() {
    String kingdomnameTaken = "Kingdom is already taken.";
    logger.error("HttpStatus: " + HttpStatus.CONFLICT
        + " KingdomNameTakenException: " + kingdomnameTaken);
    return new ErrorResponseDTO(kingdomnameTaken);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(EmailTakenException.class)
  public ErrorResponseDTO emailTaken() {
    String emailTaken = "Email is already taken.";
    logger.error("HttpStatus: " + HttpStatus.CONFLICT
        + " EmailTakenException: " + emailTaken);
    return new ErrorResponseDTO(emailTaken);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(EmailNotConfirmedException.class)
  public ErrorResponseDTO emailNotConfirmed() {
    String emailNotConfirmed = "Email not confirmed";
    logger.error("HttpStatus: " + HttpStatus.CONFLICT
        + " Email not confirmed: " + emailNotConfirmed);
    return new ErrorResponseDTO(emailNotConfirmed);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(EmailAlreadyConfirmed.class)
  public ErrorResponseDTO emailAlreadyConfirmed() {
    String emailAlreadyConfirmed = "Email alreay confirmed";
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST
        + " Email already confirmed: " + emailAlreadyConfirmed);
    return new ErrorResponseDTO(emailAlreadyConfirmed);
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(InvalidConfirmationTokenException.class)
  public ErrorResponseDTO invalidEmailCofirmationToken() {
    String invalidEmailCofirmationToken = "Invalid email confirmation token";
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST
        + " " + invalidEmailCofirmationToken);
    return new ErrorResponseDTO(invalidEmailCofirmationToken);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MultipartException.class)
  public ErrorResponseDTO userAvatarNotProvided() {
    String noImage = "User avatar not provided.";
    logger.error("HttpStatus: " + HttpStatus.BAD_REQUEST + " MultipartException: " + noImage);
    return new ErrorResponseDTO(noImage);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(AvatarUploadReadWriteException.class)
  public ErrorResponseDTO cannotInitAvatarsFolder(Exception e) {
    logger.error("HttpStatus: 500 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(AvatarUploadFiletypeException.class)
  public ErrorResponseDTO unsupportedFilePassed(Exception e) {
    logger.error("HttpStatus: 400 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }

  @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
  @ExceptionHandler(AvatarUploadFilesizeException.class)
  public ErrorResponseDTO tooBigFileUploaded(Exception e) {
    logger.error("HttpStatus: 413 " + e.getMessage());
    return new ErrorResponseDTO(e.getMessage());
  }
}
