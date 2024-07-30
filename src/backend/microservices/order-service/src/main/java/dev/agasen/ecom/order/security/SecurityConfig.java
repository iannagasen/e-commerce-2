package dev.agasen.ecom.order.security;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import dev.agasen.ecom.api.auth.resourceserver.EcomJwtAuthenticationConverter;
import dev.agasen.ecom.util.security.AuthenticationFacade;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SecurityConfig {

  private final String keySsetUri;
  private final String introspectionUri;
  private final String clientId;
  private final String clientSecret;
  private final boolean securityEnabled;

  public SecurityConfig(
    @Value("${authserver.keyset-uri}") String keySsetUri,
    @Value("${authserver.introspection-uri}") String introspectionUri,
    @Value("${resourceserver.client-id}") String clientId,
    @Value("${resourceserver.client-secret}") String clientSecret,
    @Value("${app.security.enabled}") boolean securityEnabled
  ) {
    this.keySsetUri = keySsetUri;
    this.introspectionUri = introspectionUri;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.securityEnabled = securityEnabled;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {

    http.oauth2ResourceServer(        // configure the app as resoruce server
      c -> c.jwt(                     // configure the app to use JWT
        j -> j.jwkSetUri(keySsetUri)  // configure the public key set URL that the reesource server will use to validate JWTs
              .jwtAuthenticationConverter(new EcomJwtAuthenticationConverter())
      )
    );

    /**
     *  Caused by: java.lang.ClassNotFoundException: com.nimbusds.oauth2.sdk.http.HTTPResponse
     *  will favor jwt token for now
     */
    // http.oauth2ResourceServer(
    //   c -> c.opaqueToken(
    //     token -> token.introspectionUri(introspectionUri)
    //                   .introspectionClientCredentials(clientId, clientSecret)
    //   )
    // );


    http.authorizeExchange(a -> a
        .pathMatchers("/public").permitAll()
        .anyExchange().authenticated()
    );

    return http.build();
  }


  @Bean
  public WebFluxConfigurer corsConfigurer() {
    return new WebFluxConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOrigins("http://localhost:4200")
          .allowedMethods("GET", "POST", "PUT", "DELETE")
          .allowedHeaders("*");
      }
    };
  }
 
} 
