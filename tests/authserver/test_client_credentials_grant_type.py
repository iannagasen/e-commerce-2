from _client_credentials_grant_type import ClientCredentialsGrantType

client = "ian_client_creds"
secret = "secret_cc"
redirect_uri = "my.redirect.uri"
auth_server_url = "http://localhost:8080"

client_creds_grant_type = ClientCredentialsGrantType(
  auth_server_url=auth_server_url,
  client_id=client,
  client_secret=secret,
  redirect_uri=redirect_uri,
  token_endpoint=f"{auth_server_url}/oauth2/token",
)

client_creds_grant_type.get_access_token()
