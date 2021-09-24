package com.greenfoxacademy.zelenamackatribes.utils.services;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.JwtTokenResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImpl implements JwtService {

  private static final String PREFIX = "Bearer ";
  private static final String HEADER_TYPE = "Authorization";
  private static final String SECRET_KEY = System.getenv("SECRET_KEY");
  private static final int VALID_TIME_SECONDS = 86400 * 10; // 10 days

  private TimeService timeService;
  private JwtParser jwtParser;
  private SecretKey secretKey;

  @Autowired
  public JwtServiceImpl(TimeService timeService) {
    this.secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    this.jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    this.timeService = timeService;
  }

  public String generate(UserEntity userEntity) {
    Claims claims = Jwts.claims();
    claims.put("userId", userEntity.getId());
    claims.put("userName", userEntity.getUsername());
    claims.put("kingdomId", userEntity.getKingdom().getId());
    claims.put("kingdomName", userEntity.getKingdom().getKingdomName());

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date(timeService.getTime() * 1000))
        .setExpiration(new Date(timeService.getTimeAfter(VALID_TIME_SECONDS) * 1000))
        .signWith(secretKey)
        .compact();
    return token;
  }

  public JwtTokenResponseDTO parse(String jwtToken) throws JwtException {
    if (jwtToken != null) {
      if (jwtToken.startsWith(PREFIX)) {
        if (jwtToken.replace(PREFIX, "").length() == 0) {
          throw new MalformedJwtException("JWT Token is missing.");
        }
        Claims claims = jwtParser
            .parseClaimsJws(jwtToken.replace(PREFIX, ""))
            .getBody();
        return new JwtTokenResponseDTO(
            Long.parseLong(claims.get("userId").toString()),
            claims.get("userName").toString(),
            Long.parseLong(claims.get("kingdomId").toString()),
            claims.get("kingdomName").toString()
        );
      }
      throw new InvalidTokenException("Token prefix invalid or missing");
    }
    throw new InvalidTokenException("JWT Token is missing.");
  }

  @Override
  public String extractKingdomName(String jwtToken)
      throws SignatureException, MalformedJwtException, ExpiredJwtException,
      UnsupportedJwtException,
      InvalidTokenException {
    return parse(jwtToken).getKingdomName();
  }

  @Override
  public String getPrefix() {
    return PREFIX;
  }

  @Override
  public String getHeaderType() {
    return HEADER_TYPE;
  }

  @Override
  public int getValidTimeSeconds() {
    return VALID_TIME_SECONDS;
  }
}
