from _client_credentials_grant_type import ClientCredentialsGrantType
import requests
from bs4 import BeautifulSoup

client = "ian_client_creds"
secret = "secret_cc"
auth_server_url = "http://localhost:8080"

print("Creating client credentials grant type object...")
client_creds_grant_type = ClientCredentialsGrantType(
  auth_server_url=auth_server_url,
  client_id=client,
  client_secret=secret,
  token_endpoint=f"{auth_server_url}/oauth2/token",
)

client_creds_grant_type.get_access_token()
client_creds_grant_type.introspect_token()

resource_server_url = "http://localhost:8103"
# protected_resource_url = f"{resource_server_url}/test"
protected_resource_url = f"{resource_server_url}/orders"

print("---------------------------------------------------------------------")
client_creds_grant_type.get_protected_resource_with_access_token(protected_resource_url)
