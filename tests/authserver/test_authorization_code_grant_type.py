
from _helpers import AuthorizationCodeGrantType

# pip install requests beautifulsoup4

client = "client"
secret = "secret"
redirect_uri = "my.redirect.uri"
username = "ian"
password = "pass"
auth_server_url = "http://localhost:8080"

auth_code_grant = AuthorizationCodeGrantType(
  auth_server_url=auth_server_url,
  client_id=client,
  client_secret=secret,
  redirect_uri=redirect_uri,
  token_endpoint=f"{auth_server_url}/oauth2/token",
  username=username,
  password=password
)

auth_code_grant.simulate_call_protected_resource()
auth_code_grant.extract_csrf_token_and_login()
auth_code_grant.simulate_exchanging_authcode_for_an_access_token()