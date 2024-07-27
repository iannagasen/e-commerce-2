package dev.agasen.auth_server;

import java.io.IOException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingAuthenticationEntryPoint implements AuthenticationEntryPoint {
  private final AuthenticationEntryPoint delegate;

  public LoggingAuthenticationEntryPoint(String loginUrl) {
      this.delegate = new LoginUrlAuthenticationEntryPoint(loginUrl);
  }

  // @Override
  // public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
  //     throws IOException, ServletException {
  //   // TODO Auto-generated method stub
  //   throw new UnsupportedOperationException("Unimplemented method 'commence'");
  // }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
      log.error("Authentication error: ", authException);
      delegate.commence(request, response, authException);
  }
}
