package dev.agasen.ecom.order.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

  private final String keySsetUri;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;

  public SecurityConfig(
    JwtAuthenticationConverter jwtAuthenticationConverter,
    @Value("${authserver.keyset-uri}") String keySsetUri
  ) {
    this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    this.keySsetUri = keySsetUri;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {

    http.oauth2ResourceServer(        // configure the app as resoruce server
      c -> c.jwt(                     // configure the app to use JWT
        j -> j.jwkSetUri(keySsetUri)  // configure the public key set URL that the reesource server will use to validate JWTs
              .jwtAuthenticationConverter(jwtAuthenticationConverter)
      )
    );

    http.authorizeExchange(
      a -> a.anyExchange().authenticated()
    );

    return http.build();
  }
}
