package dev.agasen.ecom.order.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.WebFluxConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class SecurityConfig {

  private final String keySsetUri;
  private final JwtAuthenticationConverter jwtAuthenticationConverter;
  private final String introspectionUri;
  private final String clientId;
  private final String clientSecret;

  public SecurityConfig(
    JwtAuthenticationConverter jwtAuthenticationConverter,
    @Value("${authserver.keyset-uri}") String keySsetUri,
    @Value("${authserver.introspection-uri}") String introspectionUri,
    @Value("${resourceserver.client-id}") String clientId,
    @Value("${resourceserver.client-secret}") String clientSecret
  ) {
    this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    this.keySsetUri = keySsetUri;
    this.introspectionUri = introspectionUri;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {

    http.oauth2ResourceServer(        // configure the app as resoruce server
      c -> c.jwt(                     // configure the app to use JWT
        j -> j.jwkSetUri(keySsetUri)  // configure the public key set URL that the reesource server will use to validate JWTs
              .jwtAuthenticationConverter(jwtAuthenticationConverter)
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
