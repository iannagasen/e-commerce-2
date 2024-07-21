from _client_credentials_grant_type import ClientCredentialsGrantType

client = "ian_client_creds"
secret = "secret_cc"
redirect_uri = "my.redirect.uri"
auth_server_url = "http://localhost:8080"

print("Creating client credentials grant type object...")
client_creds_grant_type = ClientCredentialsGrantType(
  auth_server_url=auth_server_url,
  client_id=client,
  client_secret=secret,
  redirect_uri=redirect_uri,
  token_endpoint=f"{auth_server_url}/oauth2/token",
)

client_creds_grant_type.get_access_token()


##### TESTING USING OPAQUE TOKENS #####
client = "ian_client_creds_opaque_tkn"
secret = "secret_cc_opq"

print("[OPAQUE TOKEN] Creating client credentials grant type object with...")
client_creds_grant_type_opaque = ClientCredentialsGrantType(
  auth_server_url=auth_server_url,
  client_id=client,
  client_secret=secret,
  redirect_uri=redirect_uri,
  token_endpoint=f"{auth_server_url}/oauth2/token",
)

client_creds_grant_type_opaque.get_access_token()

print("Comparing the 2 access tokens, the opaque token is much shorter than the non-opaque(JWT) token.")