import requests
from bs4 import BeautifulSoup
import base64

def extract_code_from_url(url):
  # Find the start index of the 'code' parameter
  code_start_index = url.find('code=')
  if code_start_index != -1:
    # Find the end index of the 'code' parameter
    code_start_index += len('code=')
    code_end_index = url.find('&', code_start_index)
    
    # If no '&' is found, the code is till the end of the URL
    if code_end_index == -1:  
      code_end_index = len(url)
    
    # Extract the 'code' parameter value
    return url[code_start_index:code_end_index]
  else:
    return None
  
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
    http_basic_auth = f"{self.client_id}:{self.client_secret}"
    http_basic_auth_base64 = base64.b64encode(http_basic_auth.encode()).decode()
    token_response = self._session.post(f"{self.auth_server_url}/oauth2/token", data={
      'grant_type': 'authorization_code',
      'code': self._authorization_code,
      'redirect_uri': self.redirect_uri,
      'client_id': self.client_id,
    }, headers={'Authorization': f'Basic {http_basic_auth_base64}'})
    print("Token response:")
    print(token_response.json())