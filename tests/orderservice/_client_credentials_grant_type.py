import requests
import json
import base64
from bs4 import BeautifulSoup
from _helpers import extract_code_from_url

# pip install requests beautifulsoup4

class ClientCredentialsGrantType:
  def __init__(self, client_id, client_secret, auth_server_url, token_endpoint,):
    self.client_id = client_id
    self.client_secret = client_secret
    self.auth_server_url = auth_server_url
    self.token_endpoint = token_endpoint

    self.access_token = None
    self._session = requests.Session()
    self._http_basic_auth = base64.b64encode(f"{self.client_id}:{self.client_secret}".encode()).decode()

  def get_access_token(self):
    print("Getting access token...")

    token_response = self._session.post(f"{self.auth_server_url}/oauth2/token", data={
      'grant_type': 'client_credentials',
      'client_id': self.client_id,
      'scope': 'CUSTOM'
    }, headers={'Authorization': f'Basic {self._http_basic_auth}'})

    self.access_token = token_response.json().get('access_token')
    print("Token response:")
    print(json.dumps(token_response.json(), indent=4))

  def introspect_token(self):
    print("Introspecting token...")

    introspect_response = self._session.post(f"{self.auth_server_url}/oauth2/introspect", data={
      'token': self.access_token
    }, headers={'Authorization': f'Basic {self._http_basic_auth}'})

    print("Introspect response:")
    print(json.dumps(introspect_response.json(), indent=4))   

  def get_protected_resource_with_access_token(self, resource_path):
    print("Getting protected resource with access token...")

    response = self._session.get(resource_path, headers={
      'Authorization': f'Bearer {self.access_token}'
    })

    print("Protected resource response:")
    print(json.dumps(response.json(), indent=4))
    
  def get_protected_resource_without_access_token(self, resource_path):
    print("Getting protected resource without access token...")

    response = self._session.get(resource_path)

    if response.status_code == 401:
      print("Unauthorized access. Please check your access token.")

    print(f"Protected resource response: {response.status_code} {response.reason}")


  