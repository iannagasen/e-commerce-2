import requests
import json
import base64
from bs4 import BeautifulSoup
from _helpers import extract_code_from_url

# pip install requests beautifulsoup4

class AuthorizationCodeGrantType:
  def __init__(self, client_id, client_secret, redirect_uri, auth_server_url, token_endpoint, username, password):
    self.client_id = client_id
    self.client_secret = client_secret
    self.redirect_uri = redirect_uri
    self.auth_server_url = auth_server_url
    self.token_endpoint = token_endpoint
    self.username = username
    self.password = password

    self._authorization_code = None
    self._access_token = None
    self._session = requests.Session()
    self._response_that_will_redirect_to_login = None
    self._response_from_login_that_will_redirect_to_auth_code = None


  def simulate_call_protected_resource(self):
    print("Calling protected resource...")

    # Define the endpoint URL
    endpoint = f"{self.auth_server_url}/oauth2/authorize?" \
      f"response_type=code&" \
      f"client_id={self.client_id}&" \
      f"scope=openid&" \
      f"redirect_uri={self.redirect_uri}"
    
    # Make a GET request to the endpoint
    self._response_that_will_redirect_to_login = self._session.get(endpoint, allow_redirects=True)
    print(self._response_that_will_redirect_to_login.url)
    

  def extract_csrf_token_and_login(self):
    # Extract the CSRF token from the login page
    soup = BeautifulSoup(
      self._response_that_will_redirect_to_login.text, 
      'html.parser'
    )
    csrf_token = None
    csrf_input = soup.find('input', {'name': '_csrf'})
    if csrf_input:
      csrf_token = csrf_input.get('value')
    
    # Submit the login form
    login_url = f"{self.auth_server_url}/login"
    login_data = {
        'username': self.username,
        'password': self.password,
        '_csrf': csrf_token
    }
    
    self._response_from_login_that_will_redirect_to_auth_code = self._session.post(login_url, data=login_data)
    
    redirected_url_w_auth_code = self._response_from_login_that_will_redirect_to_auth_code.url
    self._authorization_code = extract_code_from_url(redirected_url_w_auth_code)
    print(f"Extracted code: {self._authorization_code}")


  def simulate_exchanging_authcode_for_an_access_token(self):
    # this step needs the client credientials
    # what if the client credentials were compromised? Apply PKCE protection
    http_basic_auth = f"{self.client_id}:{self.client_secret}"
    http_basic_auth_base64 = base64.b64encode(http_basic_auth.encode()).decode()
    token_response = self._session.post(f"{self.auth_server_url}/oauth2/token", data={
      'grant_type': 'authorization_code',
      'code': self._authorization_code,
      'redirect_uri': self.redirect_uri,
      'client_id': self.client_id,
    }, headers={'Authorization': f'Basic {http_basic_auth_base64}'})
    print("Token response:")
    print(json.dumps(token_response.json(), indent=4))
    self._access_token = token_response.json().get('access_token')


  def get_protected_resource_with_access_token(self, resource_path):
    print("Getting protected resource with access token...")

    response = self._session.get(resource_path, headers={
      'Authorization': f'Bearer {self._access_token}'
    })

    print("Protected resource response:")
    print(json.dumps(response.json(), indent=4))

  def get_protected_resource_without_access_token(self, resource_path):
    print("Getting protected resource without access token...")

    response = self._session.get(resource_path)

    if response.status_code == 401:
      print("Unauthorized access. Please check your access token.")

    print(f"Protected resource response: {response.status_code} {response.reason}")
 

  def get_user_info(self):
    print("Getting user info... using post")
    response = self._session.post(f"{self.auth_server_url}/userinfo", headers={
      'Authorization': f'Bearer {self._access_token}'
    })
    if response.status_code == 200:
      user_info = response.text
      print("User Info:", user_info)
    else:
      print("Failed to get user info:", response.status_code, response.text)
