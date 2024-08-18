package dev.agasen.ecom.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import dev.agasen.ecom.api.auth.resourceserver.EcomJwtAuthenticationConverter;

@SpringBootApplication
@ComponentScan("dev.agasen")
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner run(InventoryRepository inventoryRepo, InventoryUpdateRepository inventoryHistoRepo) {
	// 	return args -> {
	// 		// inventoryRepo.save(new InventoryEntity("1", 1L, 10)).block();
	// 		// inventoryRepo.save(new InventoryEntity("1", 2L, 10)).block();
	// 	};
	// }
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {

    http.oauth2ResourceServer(        // configure the app as resoruce server
      c -> c.jwt(                     // configure the app to use JWT
        j -> j.jwkSetUri("http://localhost:8080/oauth2/jwks")  // configure the public key set URL that the reesource server will use to validate JWTs
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
        // DO NOT DO THIS IN PRODUCTION
        .anyExchange().permitAll()
    );

    return http.build();
  }

}
