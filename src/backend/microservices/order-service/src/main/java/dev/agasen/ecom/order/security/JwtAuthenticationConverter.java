package dev.agasen.ecom.order.security;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, Mono<CustomAuthentication>>{

  @Override
  public Mono<CustomAuthentication> convert(Jwt source) {
    List<GrantedAuthority> authorities = List.of(() -> "read");

    String priority = String.valueOf(source.getClaims().get("priority"));

    return Mono.just(new CustomAuthentication(source, authorities, priority));
  }

}
