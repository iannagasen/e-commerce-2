import requests
from bs4 import BeautifulSoup
import base64
from _helpers import extract_code_from_url

# pip install requests beautifulsoup4

client = "client"
secret = "secret"
redirect_uri = "my.redirect.uri"
username = "ian"
password = "pass"

# Define the endpoint URL
endpoint = "http://localhost:8080/oauth2/authorize?" \
  "response_type=code&" \
  f"client_id={client}&" \
  "scope=openid&" \
  f"redirect_uri={redirect_uri}"

# Create a session object to handle cookies
session = requests.Session()

# Make a GET request to the endpoint
print("Step 1: Making a GET request to the endpoint")
response = session.get(endpoint, allow_redirects=True)
print("---------------------------------------------")

# Extract the CSRF token from the login page
print("Step 2: Extracting the CSRF token from the login page")
soup = BeautifulSoup(response.text, 'html.parser')
csrf_token = None
csrf_input = soup.find('input', {'name': '_csrf'})
if csrf_input:
  csrf_token = csrf_input.get('value')

print(f"CSRF token: {csrf_token}")

print("---------------------------------------------")

# Submit the login form
login_url = "http://localhost:8080/login"
login_data = {
    'username': username,
    'password': password,
    '_csrf': csrf_token
}

print("Step 3: Submitting the login form")
login_response = session.post(login_url, data=login_data)

# Print the final URL and status code of the response
print(f"Final URL after redirection: {login_response.url}")
print(f"Status code of the login response: {login_response.status_code}")

redirected_url_w_auth_code = login_response.url
code = extract_code_from_url(redirected_url_w_auth_code)
print(f"Extracted code: {code}")

# Exchange the authorization code for an access token
# Convert client secret to base64
http_basic_auth = f"{client}:{secret}"
http_basic_auth_base64 = base64.b64encode(http_basic_auth.encode()).decode()

# Make the POST request with the modified code
token_response = session.post("http://localhost:8080/oauth2/token", data={
  'grant_type': 'authorization_code',
  'code': code,
  'redirect_uri': redirect_uri,
  'client_id': client,
}, headers={'Authorization': f'Basic {http_basic_auth_base64}'})

print("Token response:")
print(token_response.json())