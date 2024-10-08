package dev.agasen.ecom.inventory.auth.resourceserver;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;



public class EcomAuthentication extends JwtAuthenticationToken {
  /**
   * 
   * A Custom Authentication object used to reshape the JWT token
   * 
   */

   private final String priority;

   public EcomAuthentication(Jwt jwt, Collection<? extends GrantedAuthority> authorities, String priority) {
     super(jwt, authorities);
 
     // we are adding a new field to the JwtAuthenticationToken class
     // this priority field is in a claim in the JWT token
     this.priority = priority;
   }
 
   public String getPriority() {
     return priority;
   }
}
