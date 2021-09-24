
package com.greenfoxacademy.zelenamackatribes.users.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.kingdoms.services.KingdomService;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.utils.services.CleanerService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import java.io.InputStream;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerUploadAvatarTest {

  private UserEntity user;
  private Kingdom kingdom;
  private String token;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private CleanerService cleanerService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private KingdomService kingdomService;

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
  }

  @Test
  @Order(1)
  public void unauthorizedRequestNotOK() throws Exception {
    mockMvc
        .perform(
            post("/images/avatar")
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
            post("/images/avatar")
                .header("Authorization", token)
        )
        .andExpect(status().is(400))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("User avatar not provided.")));
  }

  @Test
  @Order(3)
  public void uploadedFileOtherThanImage() throws Exception {
    final InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("avatarTestFiles/iAmNotImage.txt");
    final MockMultipartFile avatar = new MockMultipartFile("image",
        "iAmNotImage.txt", "text/plain", inputStream);

    mockMvc
        .perform(
            multipart("/images/avatar")
                .file(avatar)
                .header("Authorization", token)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Given file is not JPEG, GIF or PNG image.")));
  }

  @Test
  @Order(4)
  public void uploadedFakeImageFileOrCorrupted() throws Exception {
    final InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("avatarTestFiles/iAmTextInReality.jpg");
    final MockMultipartFile avatar = new MockMultipartFile("image",
        "iAmTextInReality.jpg", "image/jpeg", inputStream);

    mockMvc
        .perform(
            multipart("/images/avatar")
                .file(avatar)
                .header("Authorization", token)
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Image file corrupted or not supported.")));
  }

  @Test
  @Order(5)
  public void uploadedFileTooBig() throws Exception {
    final InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("avatarTestFiles/iAmTooBig.jpg");
    final MockMultipartFile avatar = new MockMultipartFile(
        "image",
        user.getId() + "-original.jpg",
        MediaType.IMAGE_JPEG.toString(),
        inputStream
    );

    mockMvc
        .perform(
            multipart("/images/avatar")
                .file(avatar)
                .header("Authorization", token)
        )
        .andExpect(status().is(413))
        .andExpect(jsonPath("$.status", is("error")))
        .andExpect(jsonPath("$.message", is("Maximum file size limit exceeded (5MB)")));
  }

  @Test
  @Order(6)
  public void uploadedFileOK() throws Exception {
    final InputStream inputStream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("avatarTestFiles/iAmOK.jpg");
    final MockMultipartFile avatar = new MockMultipartFile(
        "image",
        user.getId() + "-original.jpg",
        MediaType.IMAGE_JPEG.toString(),
        inputStream
    );

    mockMvc
        .perform(
            multipart("/images/avatar")
                .file(avatar)
                .header("Authorization", token)
        )
        .andExpect(status().is(201));
  }
}