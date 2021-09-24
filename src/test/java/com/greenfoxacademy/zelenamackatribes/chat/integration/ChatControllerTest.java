package com.greenfoxacademy.zelenamackatribes.chat.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.chat.dtos.ChatMessageRequestDTO;
import com.greenfoxacademy.zelenamackatribes.chat.models.ChatMessage;
import com.greenfoxacademy.zelenamackatribes.chat.repositories.ChatRepository;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.emailRegistrationToken.ConfirmationTokenService;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestMethodOrder(OrderAnnotation.class)
public class ChatControllerTest {

  private final ObjectMapper objectMapper;
  private UserEntity user;
  private Kingdom kingdom;
  private String token;
  private ChatMessageRequestDTO message;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CleanerService cleanerService;

  @Autowired
  private JwtService jwtService;

  @MockBean
  private ConfirmationTokenService confirmationTokenService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private KingdomService kingdomService;

  @Autowired
  private ChatRepository chatRepository;

  public ChatControllerTest() {
    this.objectMapper = new ObjectMapper();
  }

  @BeforeEach
  private void init() throws Exception {
    cleanerService.cleanDatabase();
    user = new UserEntity();
    user.setUsername("Anatoli Datlov");
    user.setPassword("AZ-5");
    user = userRepository.save(user);
    kingdom = kingdomService.createKingdom(user, "V.I.Lenin npp");
    user.setKingdom(kingdom);
    token = jwtService.getPrefix() + jwtService.generate(user);
    message = new ChatMessageRequestDTO();
  }

  @Test
  @Order(1)
  public void unauthorizedRequestNotOK() throws Exception {
    mockMvc
        .perform(
            post("/message")
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Missing Authentication header.")));
  }

  @Test
  @Order(2)
  public void emptyRequestNotOK() throws Exception {
    mockMvc
        .perform(
            post("/message")
                .header("Authorization", token)
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Request body malformed or missing.")));
  }

  @Test
  @Order(3)
  public void emptyMessageNotOK() throws Exception {
    mockMvc
        .perform(
            post("/message")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new ChatMessageRequestDTO()))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Message text is required.")));
  }

  @Test
  @Order(4)
  public void messageTooLong() throws Exception {
    message.setMessage("Over 512 chars not great just terrible. "
        + "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
        + " Lorem Ipsum has been the industry's standard dummy text ever since the 1500s,"
        + " when an unknown printer took a galley of type and scrambled it to make a type "
        + "specimen book."
        + " It has survived not only five centuries, but also the leap into electronic typesetting,"
        + " remaining essentially unchanged. It was popularised in the 1960s with the release of "
        + "Letraset "
        + "sheets containing Lorem Ipsum passages, and more recently with desktop publishing "
        + "software "
        + "like Aldus PageMaker including versions of Lorem Ipsum");
    mockMvc
        .perform(
            post("/message")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(message))
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Message too long, maximum 512 characters allowed.")));
  }

  @Test
  @Order(5)
  public void messageOKandSaved() throws Exception {
    message.setMessage("pump feedwater 2 d core");
    mockMvc
        .perform(
            post("/message")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(message))
        )
        .andExpect(status().is(201));
  }

  @Test
  @Order(6)
  public void getSavedMessages() throws Exception {
    String message = "raise the power";
    ChatMessage chatMessageEntity = new ChatMessage(message, user);

    chatRepository.save(chatMessageEntity);

    mockMvc
        .perform(
            get("/messages")
                .header("Authorization", token)
        )
        .andExpect(status().is(200))
        .andExpect(jsonPath("$[0].message", is(message)));
  }

  @Test
  @Order(7)
  public void doNotGetOldMessages() throws Exception {
    String textOld = "I saw the message";
    ChatMessage chatMessageOld = new ChatMessage(textOld, user, LocalDateTime.now().minusHours(36));
    String textNew = "No, You didnt, because its not there";
    ChatMessage chatMessageNew = new ChatMessage(textNew, user, LocalDateTime.now());

    chatRepository.save(chatMessageNew);
    chatRepository.save(chatMessageOld);

    mockMvc
        .perform(
            get("/messages")
                .header("Authorization", token)
        )
        .andExpect(status().is(200))
        .andExpect(jsonPath("$[0].message", is(textNew)))
        .andExpect(jsonPath("$.length()", is(1)));
  }
}
