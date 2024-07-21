from _client_credentials_grant_type import ClientCredentialsGrantType
import requests
from bs4 import BeautifulSoup

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
client_creds_grant_type.introspect_token()

resource_server_url = "http://localhost:8103"
protected_resource_url = f"{resource_server_url}/test"

client_creds_grant_type.get_protected_resource_without_access_token(protected_resource_url)
client_creds_grant_type.get_protected_resource_with_access_token(protected_resource_url)
