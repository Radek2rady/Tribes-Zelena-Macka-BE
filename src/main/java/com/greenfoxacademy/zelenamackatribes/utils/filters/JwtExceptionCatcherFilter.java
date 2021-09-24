package com.greenfoxacademy.zelenamackatribes.utils.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.zelenamackatribes.globalExceptionHandling.dtos.ErrorResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtExceptionCatcherFilter extends OncePerRequestFilter {

  private static Logger logger = LogManager.getLogger(JwtExceptionCatcherFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (MalformedJwtException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      logger.error(response + " " + e.getMessage());
      sendErrorDTO(response, e.getMessage());
    } catch (ExpiredJwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      String tokenExpired = "JWT token has expired.";
      logger.error(response + " " + tokenExpired);
      sendErrorDTO(response, tokenExpired);
    } catch (SignatureException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      String tokenNotValid = "JWT token signature is not valid.";
      logger.error(response + " " + tokenNotValid);
      sendErrorDTO(response, tokenNotValid);
    } catch (UnsupportedJwtException e) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      String unsuportJWT = "Unsupported JWT.";
      logger.error(response + " " + unsuportJWT);
      sendErrorDTO(response, unsuportJWT);
    } catch (JwtException e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      String unkownError = "Unknown JWT error.";
      logger.error(response + " " + unkownError);
      sendErrorDTO(response, unkownError);
    }
  }

  private void sendErrorDTO(HttpServletResponse response, String message) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String json = mapper.writeValueAsString(new ErrorResponseDTO(message));
    response.setContentType("application/json");
    response.getWriter().write(json);
  }
}
