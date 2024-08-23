package dev.agasen.ecom.order.security;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import dev.agasen.ecom.order.auth.resourceserver.EcomJwtAuthenticationConverter;
import dev.agasen.ecom.util.security.AuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;



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
              .jwtAuthenticationConverter(new EcomJwtAuthenticationConverter()))
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

    // http.cors((Customizer<CorsSpec>) CorsSpec::disable);

    return http.build();
  }


  // @Bean
  // public WebFluxConfigurer corsConfigurer() {
  //   return new WebFluxConfigurer() {
  //     @Override
  //     public void addCorsMappings(CorsRegistry registry) {
  //       registry.addMapping("/**")
  //         // .allowCredentials(true)
  //         .allowedOrigins("*")
  //         .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
  //         .allowedHeaders("*");
        
  //     }
  //   };
  // }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public WebFilter corsFilter() {

    final String ALLOWED_HEADERS = "*";
    final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
    final String ALLOWED_ORIGIN = "*";
    final String MAX_AGE = "3600";

    return (ServerWebExchange ctx, WebFilterChain chain) -> {
      ServerHttpRequest request = ctx.getRequest();
      if (CorsUtils.isCorsRequest(request)) {
        ServerHttpResponse response = ctx.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
        headers.add("Access-Control-Max-Age", MAX_AGE);
        headers.add("Access-Control-Allow-Headers",ALLOWED_HEADERS);
        if (request.getMethod() == HttpMethod.OPTIONS) {
          response.setStatusCode(HttpStatus.OK);
          return Mono.empty();
        }
      }
      return chain.filter(ctx);
    };
  }

} 
