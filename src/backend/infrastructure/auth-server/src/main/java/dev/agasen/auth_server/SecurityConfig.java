package dev.agasen.auth_server;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfig {
  /**
   * 
   * 1. Authentication Server SecurityFilterChain
   * 2. Default Application SecurityFilterChain
   * 3. User Management - UserDetailsService
   * 4. User Management - PasswordEncoder
   * 5. Client Management - RegisteredClientRepository
   * 6. Key-Pair Management - JWK Source
   * 7. Authorization Server Settings
   * 8. OAuth2TokenCustomizer
   * 
   * 
   * OPAQUE TOKEN:
   *  - does not contain data(that is why it is short)
   *  - Q: how can someone validate it and get more details about the client (and potentially the user) 
   *    for whom the auth server generated it?
   *    - easiest way: ask the auth server
   *    - the auth server exposes an endpoint where one can send a request with the token
   *      - TOKEN INTROSPECTION
   * 
   * CLAIMS
   *  - core information that the token carries
   *    
   * 
   * 
   * TODO: remove in memory userDetails and clientRepository - should be retrieved from db
   */


  @Bean
  @Order(1)
  public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {

    // set the minimal configuration for the auth server
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

    // enable OpenID connect
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
      .oidc(Customizer.withDefaults());
      
    // specify the login page for users
    http.exceptionHandling(e -> 
      e.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
    );

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    
    http.formLogin(Customizer.withDefaults());

    http.authorizeHttpRequests(req -> {
      req.anyRequest().authenticated();
    });

    return http.build();
  }


  @Bean
  public UserDetailsService userDetailsService() {
    // We need the UserDetailsService and PasswordEncoder to manage granttypes
    UserDetails userDetails = User.withUsername("ian")
      .password("pass")
      .roles("USER")
      .build();

    return new InMemoryUserDetailsManager(userDetails);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();
  }

  @Bean
  public RegisteredClientRepository clientRepository() {
    // We need the RegisterClientRepository to manage clients
    // Q: Why is the need of UserDetailsService then???
    RegisteredClient rc_w_auth_code = RegisteredClient
      .withId(UUID.randomUUID().toString())                   // internal id
      .clientId("client")                                     // username
      .clientSecret("secret")                                 // password
      .clientAuthenticationMethod(                            // 
        ClientAuthenticationMethod.CLIENT_SECRET_BASIC
      )
      .authorizationGrantType(                                // grant type allowed by as for this client
        AuthorizationGrantType.AUTHORIZATION_CODE
      )
      .redirectUri("https://www.manning.com/authorized")      // list of uri where the as will redirect the user with the auth code
      .redirectUri("my.redirect.uri")
      .scope(OidcScopes.OPENID)                               // defines the purpose for the request of an access token
      .build();


    RegisteredClient rc_w_client_creds = RegisteredClient
      .withId(UUID.randomUUID().toString())
      .clientId("ian_client_creds")
      .clientSecret("secret_cc")                              // USING SAME SECRET FOR THE DIFF CLIENTS WILL CAUSE AN EXCEPTION
      .clientAuthenticationMethod(
        ClientAuthenticationMethod.CLIENT_SECRET_BASIC
      )
      .authorizationGrantType(
        AuthorizationGrantType.CLIENT_CREDENTIALS              // Client Credentials workflow
      )
      .scope("CUSTOM")
      .build();

    // opaque tokens are shorter and does not contain data
    RegisteredClient rc_w_client_creds_n_opaque_tkns = RegisteredClient
      .withId(UUID.randomUUID().toString())
      .clientId("ian_client_creds_opaque_tkn")
      .clientSecret("secret_cc_opq")
      .clientAuthenticationMethod(
        ClientAuthenticationMethod.CLIENT_SECRET_BASIC
      )
      .authorizationGrantType(
        AuthorizationGrantType.CLIENT_CREDENTIALS
      )
      .tokenSettings(
        TokenSettings.builder()
          .accessTokenFormat(OAuth2TokenFormat.REFERENCE)       // COnfiguring the client to use opaque tokens
          .accessTokenTimeToLive(Duration.ofHours(12))
          .build()
      )
      .scope("CUSTOM")
      .build();

    RegisteredClient order_service_client = RegisteredClient
      .withId(UUID.randomUUID().toString())
      .clientId("order_service_client")
      .clientSecret("order_service_client_secret")
      .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
      .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
      .build();

    return new InMemoryRegisteredClientRepository(
      rc_w_auth_code, 
      rc_w_client_creds,
      rc_w_client_creds_n_opaque_tkns,
      order_service_client
    );
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
    /**
     * JWKSource is the object providing key management for Spring Security AuthorizationServer
     * NOTE: !!!! IRL, the app would read read the keys from a location where theyre safle stored (such as vault)
     * 
     * "jwks_uri": "http://localhost:8080/oauth2/jwks", <-- KeySet endpoint
     *    - provieds the public keys for the auth server
     * Sample output:
     *  "keys": [
     *   {
     *    "kty": "RSA",
     *    "e": "AQAB",
     *    "kid": "b1e7b4b7-7b3b-4b3b-8b3b-3b3b3b3b3b3b",
     *    "alg": "RS256",
     *    "n": "rEWxMab_kgBv-ardz7qivEtpvKuG......"
     *   }
     * ]
     * 
     */
    
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    
    keyPairGenerator.initialize(2048);

    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    RSAPublicKey publicKey    = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey  = (RSAPrivateKey) keyPair.getPrivate();

    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();

    JWKSet jwkSet = new JWKSet(rsaKey);

    return new ImmutableJWKSet<>(jwkSet);
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    /**
     * AuthorizationServerSettings allows us to customize all the endpoints paths that thte authserver exposes.
     */

     // endpoints paths will get some defaults
     return AuthorizationServerSettings
        .builder()
        .build();
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
    /**
     * Allows us to customize the JWT token
     *  - add custom claims
     *    - claims are the data that the token carries
     *    - claims are the core information that the token carries
     */
    return context -> {
      Builder claims = context.getClaims();
      /**
       * With this change, the access tokens now contain a custom 'priority' field
       * 
       * eyJraWQiOiJhNTQwMjdmNC01MzI0LTQxMjYtYWVhYi05YjIxNDVmZDExNzkiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJpYW5fY2xpZW50X2NyZWRzIiwiYXVkIjoiaWFuX2NsaWVudF9jcmVkcyIsIm5iZiI6MTcyMTY0ODQ3Miwic2NvcGUiOlsiQ1VTVE9NIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MCIsInByb3BlcnR5IjoiSElHSCIsImV4cCI6MTcyMTY0ODc3MiwiaWF0IjoxNzIxNjQ4NDcyLCJqdGkiOiJmMWYzZWJiMC04ZmJmLTQ1ZDMtOWQyYS1mM2Q4YzQ1ZDBkNTgifQ.aa_oG3VIKpFhiO2uZO7Je3UDMEA59fkoLmMafeMEwj77ep4Plio89tVcokhEiKavkh8iowdAVhzafNDa5i7MRDHxVzkuJtZyh4CH34MmkdoCVniik85iBCQiA2rSKzmVLJ9qKrAOrsMF9xP8ttCVwu0GRvbUiljrZbaeZc5tgClOef0X2VUZNOFCbl6tdHFdFhQvNgvcRBdMxZzS8XeCTWum8o8vmBHbhud_HsxNHN_ICJkGPpdn2eZjoG1F1vgnraErqiElF4qh9fVlFs55-szgsBv5u0AFhbIfwPPC-ip-G4JYcf9IY7r592oElHT1SDUGPxzVjtA_P-9RX8ay5w
       * 
       * if you decode the token, you will see the custom claim
       * 
       * {
       *   "sub": "ian_client_creds",
       *   "aud": "ian_client_creds",
       *   "nbf": 1721648472,
       *   "scope": [
       *     "CUSTOM"
       *   ],
       *   "iss": "http://localhost:8080",
       *   "property": "HIGH",        <----------------- custom claim
       *   "exp": 1721648772,
       *   "iat": 1721648472,
       *   "jti": "f1f3ebb0-8fbf-45d3-9d2a-f3d8c45d0d58"
       * }
       * 
       * or you could use introspect endpoint /oauth2/introspect to get the details
       * of the access token
       */
      claims.claim("property", "HIGH");
    };
  }

}

