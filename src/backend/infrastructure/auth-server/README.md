

### endpoint paths the authorization server exposes
**/.well-known/openid-configuration**


```json
{
  
  "issuer": "http://localhost:8080",
  
  // authorization endpoint to which the a client will redirect the user to authenticate
  "authorization_endpoint": "http://localhost:8080/oauth2/authorize",

  "device_authorization_endpoint": "http://localhost:8080/oauth2/device_authorization",

  // the token endpoint the client will call to request an access token
  "token_endpoint": "http://localhost:8080/oauth2/token",
  "token_endpoint_auth_methods_supported": [
    "client_secret_basic",
    "client_secret_post",
    "client_secret_jwt",
    "private_key_jwt",
    "tls_client_auth",
    "self_signed_tls_client_auth"
  ],

  // the key-set endpoint a resource server will call to get the public keys
  // it can use to validate tokens
  "jwks_uri": "http://localhost:8080/oauth2/jwks",
  "userinfo_endpoint": "http://localhost:8080/userinfo",
  "end_session_endpoint": "http://localhost:8080/connect/logout",
  "response_types_supported": [
    "code"
  ],
  "grant_types_supported": [
    "authorization_code",
    "client_credentials",
    "refresh_token",
    "urn:ietf:params:oauth:grant-type:device_code",
    "urn:ietf:params:oauth:grant-type:token-exchange"
  ],
  "revocation_endpoint": "http://localhost:8080/oauth2/revoke",
  "revocation_endpoint_auth_methods_supported": [
    "client_secret_basic",
    "client_secret_post",
    "client_secret_jwt",
    "private_key_jwt",
    "tls_client_auth",
    "self_signed_tls_client_auth"
  ],

  // the introspection endpoint a resource server can call to validate opaque tokens
  "introspection_endpoint": "http://localhost:8080/oauth2/introspect",
  "introspection_endpoint_auth_methods_supported": [
    "client_secret_basic",
    "client_secret_post",
    "client_secret_jwt",
    "private_key_jwt",
    "tls_client_auth",
    "self_signed_tls_client_auth"
  ],
  "code_challenge_methods_supported": [
  "S256"
  ],
  "tls_client_certificate_bound_access_tokens": true,
  "subject_types_supported": [
    "public"
  ],
  "id_token_signing_alg_values_supported": [
    "RS256"
  ],
  "scopes_supported": [
    "openid"
  ]
}
```


```bash
http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=https://www.manning.com/authorized&code_challenge=QYPAZ5NU8yvtlQ9erXrUYR-T5AGCjCF47vN-KsaI2A8&code_challenge_method=S256

// OR

curl -i -L "http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=https://www.manning.com/authorized&code_challenge=QYPAZ5NU8yvtlQ9erXrUYR-T5AGCjCF47vN-KsaI2A8&code_challenge_method=S256"
```