package com.greenfoxacademy.zelenamackatribes.users.unit;

import com.greenfoxacademy.zelenamackatribes.users.exceptions.AvatarUploadException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadFilesizeException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadFiletypeException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import com.greenfoxacademy.zelenamackatribes.users.services.AvatarService;
import com.greenfoxacademy.zelenamackatribes.users.services.AvatarServiceImpl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

@TestMethodOrder(OrderAnnotation.class)
public class AvatarServiceTest {
  private UserRepository userRepository;
  private AvatarService avatarService;

  @BeforeEach
  public void init() throws AvatarUploadException {
    userRepository = Mockito.mock(UserRepository.class);
    avatarService = new AvatarServiceImpl(userRepository);
  }

  @Test
  @Order(1)
  public void passedFileOtherThanImage() {
    Exception exception = Assertions.assertThrows(
        AvatarUploadFiletypeException.class,
        () -> avatarService.upload(prepareTestFile("iAmNotImage.txt", "text/plain"), 0)
    );
    Assertions.assertEquals("Given file is not JPEG, GIF or PNG image.", exception.getMessage());
  }

  @Test
  @Order(2)
  public void passedFileThatFakesItsType() {
    Exception exception = Assertions.assertThrows(
        AvatarUploadFiletypeException.class,
        () -> avatarService.upload(prepareTestFile("iAmTextInReality.jpg", "text/plain"), 0)
    );
    Assertions.assertEquals("Given file is not JPEG, GIF or PNG image.", exception.getMessage());
  }

  @Test
  @Order(3)
  public void passedFileThatFakesItsTypeWithMime() {
    Exception exception = Assertions.assertThrows(
        AvatarUploadFiletypeException.class,
        () -> avatarService.upload(prepareTestFile("iAmTextInReality.jpg"), 0)
    );
    Assertions.assertEquals("Image file corrupted or not supported.", exception.getMessage());
  }

  @Test
  @Order(4)
  public void passedBigFile() {
    Exception exception = Assertions.assertThrows(
        AvatarUploadFilesizeException.class,
        () -> avatarService.upload(prepareTestFile("iAmTooBig.jpg"), 0)
    );
    Assertions.assertEquals("Maximum file size limit exceeded (5MB)", exception.getMessage());
  }

  @Test
  @Order(5)
  public void passedOKFile() throws AvatarUploadException, IOException {
    removeFile("0-original.jpg");
    removeFile("0-avatar.jpg");

    Mockito
        .when(userRepository.getOne(0L))
        .thenReturn(new UserEntity());

    avatarService.upload(prepareTestFile("iAmOK.jpg"), 0);

    Assertions.assertTrue(checkExistenceAndValidity("0-original.jpg"));
    Assertions.assertTrue(checkExistenceAndValidity("0-avatar.jpg"));
  }

  private MockMultipartFile prepareTestFile(String testFile, String mimeType) throws IOException {
    return new MockMultipartFile(
        "image",
        "test-original.jpg",
        mimeType,
        Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("avatarTestFiles/" + testFile)
    );
  }

  private MockMultipartFile prepareTestFile(String testFile) throws IOException {
    return prepareTestFile(testFile, "image/jpeg");
  }

  private void removeFile(String filename) {
    Path path = Paths.get("resources/static/avatars/" + filename);
    File file = path.toFile();
    if (file.exists()) {
      file.delete();
    }
  }

  private boolean checkExistenceAndValidity(String filename) {
    Path path = Paths.get("resources/static/avatars/" + filename);
    File file = path.toFile();
    return file.exists() && file.canRead() && file.length() > 0;
  }
}
