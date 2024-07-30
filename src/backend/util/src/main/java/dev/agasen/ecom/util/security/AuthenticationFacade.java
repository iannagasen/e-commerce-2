package dev.agasen.ecom.util.security;

import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFacade {
  
  public Mono<Authentication> getAuthentication() {
    log.info("context: {}", SecurityContextHolder.getContext());
    return ReactiveSecurityContextHolder.getContext().map((Function<? super SecurityContext, ? extends Authentication>) SecurityContext::getAuthentication);
  }

  public void setAuthentication(Authentication authentication) {
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
  
}
