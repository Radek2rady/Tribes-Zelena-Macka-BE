package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.JwtTokenResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

public interface JwtService {

  String generate(UserEntity userEntity);

  JwtTokenResponseDTO parse(String jwtToken)
      throws SignatureException, MalformedJwtException, ExpiredJwtException,
      UnsupportedJwtException,
      InvalidTokenException;

  String extractKingdomName(String jwtToken)
      throws SignatureException, MalformedJwtException, ExpiredJwtException,
      UnsupportedJwtException,
      InvalidTokenException;

  String getPrefix();

  String getHeaderType();

  int getValidTimeSeconds();
}
