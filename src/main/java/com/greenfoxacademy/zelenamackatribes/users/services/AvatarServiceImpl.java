package com.greenfoxacademy.zelenamackatribes.users.services;

import com.greenfoxacademy.zelenamackatribes.users.exceptions.AvatarUploadException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadFilesizeException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadFiletypeException;
import com.greenfoxacademy.zelenamackatribes.users.exceptions.avatarUploadException.AvatarUploadReadWriteException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.users.repositories.UserRepository;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AvatarServiceImpl implements AvatarService {

  private static final String AVATARS_DIR = "resources/static/avatars/";
  private static final List<String> ALLOWED_TYPES =
      Arrays.asList("image/png", "image/jpeg", "image/gif");
  private static final int AVATAR_SIZE = 256;
  private static final long MAX_FILE_SIZE = 5242880;
  private static final Logger logger = LogManager.getLogger(AvatarServiceImpl.class);
  private UserRepository userRepository;

  @Autowired
  public AvatarServiceImpl(UserRepository userRepository)
      throws AvatarUploadException {
    this.userRepository = userRepository;
    init();
  }

  @Override
  public void upload(MultipartFile multipartFile, long userId)
      throws AvatarUploadException {
    String incomingFileName = multipartFile.getOriginalFilename();
    String incomingFileExtension = incomingFileName
        .substring(incomingFileName.lastIndexOf(".")).toLowerCase();
    String savedFileName = userId + "-original" + incomingFileExtension;
    validate(multipartFile);
    saveOriginal(multipartFile, savedFileName);
    createResizedJpeg(savedFileName, userId + "-avatar");
    assignToUser(userId);
  }

  private void init() throws AvatarUploadException {
    Path uploadFolder = Paths.get(AVATARS_DIR);
    if (!Files.exists(uploadFolder)) {
      try {
        Files.createDirectories(uploadFolder);
        logger.info("AvatarService: created " + AVATARS_DIR + " folder");
      } catch (IOException e) {
        throw new AvatarUploadReadWriteException("Cannot create folder for storing avatars");
      }
    }
  }

  private void validate(MultipartFile multipartFile) throws AvatarUploadException {
    if (multipartFile.getSize() > MAX_FILE_SIZE) {
      throw new AvatarUploadFilesizeException("Maximum file size limit exceeded (5MB)");
    }
    if (!ALLOWED_TYPES.contains(multipartFile.getContentType())) {
      throw new AvatarUploadFiletypeException("Given file is not JPEG, GIF or PNG image.");
    }
    BufferedImage bi;
    try {
      bi = ImageIO.read(multipartFile.getInputStream());
      bi.flush();
    } catch (IOException | NullPointerException e) {
      throw new AvatarUploadFiletypeException("Image file corrupted or not supported.");
    }
  }

  private void saveOriginal(MultipartFile multipartFile, String fileName)
      throws AvatarUploadException {
    try (InputStream originalFileInputStream = multipartFile.getInputStream()) {
      Path originalFilePath = Paths.get(AVATARS_DIR).resolve(fileName);
      Files.copy(originalFileInputStream, originalFilePath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException ioe) {
      throw new AvatarUploadReadWriteException("Could not save image file: " + fileName);
    }
  }

  private void createResizedJpeg(String originalFileName, String newFileName)
      throws AvatarUploadException {
    Path originalFilePath = Paths.get(AVATARS_DIR).resolve(originalFileName);
    File sourceFile = new File(originalFilePath.toString());
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(sourceFile);
    } catch (IOException e) {
      throw new AvatarUploadReadWriteException("Could not load original image file: "
          + originalFilePath);
    }
    BufferedImage outputImage = Scalr.resize(bufferedImage, AVATAR_SIZE);
    Path newFilePath = Paths.get(AVATARS_DIR).resolve(newFileName + ".jpg");
    File newImageFile = newFilePath.toFile();
    try {
      ImageIO.write(outputImage, "jpg", newImageFile);
    } catch (IOException e) {
      throw new AvatarUploadReadWriteException("Could not create resized avatar version: "
          + newFilePath);
    }
    outputImage.flush();
  }

  private void assignToUser(long userId) {
    UserEntity user = userRepository.getOne(userId);
    user.setAvatar(Paths.get(AVATARS_DIR).resolve(userId + "-avatar.jpg").toString());
    userRepository.save(user);
  }
}
