from _auth_code_grant_type import AuthorizationCodeGrantType
import time


client = "angular_client"
secret = "angular_client_secret"
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

# Step 1: Call the protected resource, Step 2: this will redirect to the login page
auth_code_grant.simulate_call_protected_resource()
# Step 3: Extract the CSRF token and login, Step 4: this will redirect to the provided redirect URI
auth_code_grant.extract_csrf_token_and_login()
# Step 5: Exchange the authorization code for an access token, Step 6: this will return the access token
auth_code_grant.simulate_exchanging_authcode_for_an_access_token()


resource_server_url = "http://localhost:8103"
protected_resource_url = f"{resource_server_url}/test"

auth_code_grant.get_protected_resource_without_access_token(protected_resource_url)
auth_code_grant.get_protected_resource_with_access_token(protected_resource_url)
auth_code_grant.get_user_info()