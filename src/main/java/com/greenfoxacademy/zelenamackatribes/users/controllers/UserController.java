package com.greenfoxacademy.zelenamackatribes.users.controllers;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValueNotFoundException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.DefaultValuesFileMissingException;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.defaultValuesFileException.IncorrectDefaultValueTypeException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.exceptions.KingdomNotFoundException;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserConfirmationTokenResponseDTO;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserLoginRequestDTO;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserLoginResponseDTO;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserRegisterRequestDTO;
import com.greenfoxacademy.zelenamackatribes.users.dtos.UserRegisterResponseDTO;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.AvatarUploadException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailAlreadyConfirmed;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.EmailNotConfirmedException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidConfirmationTokenException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.InvalidUserCredentialsException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.SendValidationEmailException;
import com.greenfoxacademy.zelenamackatribes.users.services.AvatarService;
import com.greenfoxacademy.zelenamackatribes.users.services.UserService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import javax.mail.MessagingException;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {

  private static final Logger logger = LogManager.getLogger(UserController.class);
  private final ModelMapper modelMapper;
  private UserService userService;
  private AvatarService avatarService;
  private JwtService jwtService;

  @Autowired
  public UserController(UserService userService, ModelMapper modelMapper,
      AvatarService avatarService, JwtService jwtService) {
    this.userService = userService;
    this.modelMapper = modelMapper;
    this.avatarService = avatarService;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public UserLoginResponseDTO login(@Valid @RequestBody UserLoginRequestDTO dto)
      throws InvalidUserCredentialsException, KingdomNotFoundException,
      EmailNotConfirmedException {
    logger.info("POST /login " + dto.getUsername());
    return new UserLoginResponseDTO(userService.login(dto.getUsername(), dto.getPassword()));
  }

  @PostMapping("/register")
  public ResponseEntity<UserRegisterResponseDTO> registerNewUser(
      @Valid @RequestBody UserRegisterRequestDTO userRegisterRequestDTO)
      throws Exception {
    var newRegisteredUser = userService.createNewUser(userRegisterRequestDTO.getUsername(),
        userRegisterRequestDTO.getPassword(), userRegisterRequestDTO.getEmail(),
        userRegisterRequestDTO.getKingdomName());
    logger.info(
        "POST /register " + HttpStatus.CREATED + " " + userRegisterRequestDTO.getUsername());
    return new ResponseEntity<>(modelMapper.map(newRegisteredUser, UserRegisterResponseDTO.class),
        HttpStatus.CREATED);
  }

  @GetMapping(path = "/confirm")
  public ResponseEntity<?> confirm(@RequestParam("token") String token)
      throws InvalidConfirmationTokenException, MessagingException,
      SendValidationEmailException, EmailAlreadyConfirmed, IncorrectDefaultValueTypeException,
      DefaultValueNotFoundException, DefaultValuesFileMissingException {
    userService.confirmToken(token);
    logger.info("GET /confirm " + token + " confirmed");
    return new ResponseEntity<>(new UserConfirmationTokenResponseDTO("Email confirmed"),
        HttpStatus.ACCEPTED);
  }

  @PostMapping("/images/avatar")
  public ResponseEntity uploadAvatar(@RequestHeader(value = "Authorization") String token,
      @RequestParam("image") MultipartFile multipartFile) throws AvatarUploadException {
    long userId = jwtService.parse(token).getId();
    avatarService.upload(multipartFile, userId);
    logger.info("POST /images/avatar: avatar for user with ID " + userId + " created");
    return new ResponseEntity(HttpStatus.CREATED);
  }
}
