package com.greenfoxacademy.zelenamackatribes.utils.unit;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.JwtTokenResponseDTO;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.exceptions.InvalidTokenException;
import com.greenfoxacademy.zelenamackatribes.kingdoms.models.Kingdom;
import com.greenfoxacademy.zelenamackatribes.users.models.UserEntity;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtServiceImpl;
import com.greenfoxacademy.zelenamackatribes.utils.services.TimeService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class JwtServiceTest {

  private TimeService timeService;
  private JwtService jwtService;
  private String expectedTokenStart;
  private UserEntity user;
  private Kingdom kingdom;
  private String token;
  private Pattern pattern;
  private Matcher matcher;

  @BeforeEach
  public void init() {
    this.expectedTokenStart = "eyJhbGciOiJIUzI1NiJ9";
    this.pattern = Pattern.compile("^[^\\.]+\\.[^\\.]+\\.[^\\.]+$");
    user = new UserEntity();
    kingdom = new Kingdom();
    kingdom.setKingdomName("Slovensko");
    kingdom.setId(1L);
    user.setId(1L);
    user.setUsername("Testovic");
    user.setKingdom(kingdom);
    user.setIsEmailConfirmed(true);

    timeService = Mockito.mock(TimeService.class);
    jwtService = new JwtServiceImpl(timeService);

    Mockito
        .when(timeService.getTime())
        .thenReturn((long) (Integer.MAX_VALUE - jwtService.getValidTimeSeconds() - 1));
    Mockito
        .when(timeService.getTimeAfter(jwtService.getValidTimeSeconds()))
        .thenReturn((long) (Integer.MAX_VALUE - 1));

    token = jwtService.generate(user);
    matcher = pattern.matcher(token);
  }

  @Test
  @Order(1)
  public void generate() {
    Assertions.assertEquals(expectedTokenStart, token.substring(0, token.indexOf(".")));
    Assertions.assertTrue(matcher.find());
  }

  @Test
  @Order(2)
  public void tokenHasValidData() throws Exception {
    SecretKey secretKey = Keys.hmacShaKeyFor(
        System.getenv("SECRET_KEY").getBytes(StandardCharsets.UTF_8));
    JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    Claims claims = jwtParser
        .parseClaimsJws(token.replace("Bearer ", ""))
        .getBody();
    long startTime = Long.parseLong(claims.get("iat").toString());
    long endTime = Long.parseLong(claims.get("exp").toString());

    Assertions.assertEquals(user.getUsername(), claims.get("userName").toString());
    Assertions.assertEquals(user.getId(), Long.valueOf(claims.get("userId").toString()));
    Assertions
        .assertEquals(user.getKingdom().getKingdomName(), claims.get("kingdomName").toString());
    Assertions
        .assertEquals(user.getKingdom().getId(), Long.valueOf(claims.get("kingdomId").toString()));
    Assertions.assertEquals(60 * 60 * 24 * 10, endTime - startTime);
  }

  @Test
  @Order(3)
  public void parsing() throws JwtException {
    String token = jwtService.getPrefix() + jwtService.generate(user);
    JwtTokenResponseDTO jwtTokenDTO = jwtService.parse(token);
    Assertions.assertEquals(jwtTokenDTO.getId(), user.getId());
    Assertions.assertEquals(jwtTokenDTO.getUsername(), user.getUsername());
    Assertions.assertEquals(jwtTokenDTO.getKingdomId(), user.getKingdom().getId());
    Assertions.assertEquals(jwtTokenDTO.getKingdomName(), user.getKingdom().getKingdomName());
  }

  @Test
  @Order(4)
  public void tokenSignatureIsMalformed() {
    String token = jwtService.getPrefix() + jwtService.generate(user);

    Exception exception = Assertions.assertThrows(
        SignatureException.class,
        () -> jwtService.parse(token.substring(0, token.length() - 1))
    );
  }

  @Test
  @Order(5)
  public void tokenBearerPrefixIsMissing() {
    Exception exception = Assertions.assertThrows(
        InvalidTokenException.class,
        () -> jwtService.parse(token)
    );
  }

  @Test
  @Order(6)
  public void tokenHeaderIsMalformed() {
    Exception exception = Assertions.assertThrows(
        MalformedJwtException.class,
        () -> jwtService.parse(jwtService.getPrefix() + token.substring(1))
    );
  }

  @Test
  @Order(7)
  public void tokenBodyIsMalformed() {
    Pattern pattern = Pattern.compile("^.+(\\.)");

    String token = jwtService.getPrefix() + jwtService.generate(user);
    Matcher matcher = pattern.matcher(token);
    matcher.find();
    int separatorIndex = matcher.end();

    String malformedToken =
        token.substring(0, separatorIndex - 2) + token.substring(separatorIndex - 1);

    Exception exception = Assertions.assertThrows(
        SignatureException.class,
        () -> jwtService.parse(malformedToken)
    );
  }
}
