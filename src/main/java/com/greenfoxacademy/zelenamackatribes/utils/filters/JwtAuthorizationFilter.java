package com.greenfoxacademy.zelenamackatribes.utils.filters;

import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.JwtTokenResponseDTO;
import com.greenfoxacademy.zelenamackatribes.utils.services.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private JwtService jwtService;

  private static Logger logger = LogManager.getLogger(JwtAuthorizationFilter.class);

  @Autowired
  public JwtAuthorizationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain
  ) throws ServletException, IOException, JwtException {
    if (headerCheck(request)) {
      String header = request.getHeader(jwtService.getHeaderType());
      JwtTokenResponseDTO dto = jwtService.parse(header);
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
          dto.getUsername(), null, null
      );
      SecurityContextHolder.getContext().setAuthentication(auth);
    } else {
      SecurityContextHolder.clearContext();
    }
    chain.doFilter(request, response);
  }

  private boolean headerCheck(HttpServletRequest request) {
    Optional<String> authorizationHeader = Optional
        .ofNullable(request.getHeader(jwtService.getHeaderType()));
    if (authorizationHeader.isPresent()) {
      if (authorizationHeader.get().length() == 0) {
        String noContent = "Authentication header without content.";
        logger.error(noContent);
        throw new MalformedJwtException(noContent);
      }
      if (authorizationHeader.get().startsWith(jwtService.getPrefix().trim())) {
        if (authorizationHeader.get().trim().length() == jwtService.getPrefix().trim().length()) {
          String missingToken = "Missing Authentication JWT Token.";
          logger.error(missingToken);
          throw new MalformedJwtException(missingToken);
        }
        return true;
      }
      String missingBearer = "Missing JWT token bearer.";
      logger.error(missingBearer);
      throw new MalformedJwtException(missingBearer);
    }
    String missingHeader = "Missing Authentication header.";
    logger.error(missingHeader);
    throw new MalformedJwtException(missingHeader);
  }
}
