package com.greenfoxacademy.zelenamackatribes.users.services;

import com.greenfoxacademy.zelenamackatribes.users.exceptions.AvatarUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {

  void upload(MultipartFile multipartFile, long userId)
      throws AvatarUploadException;
}
