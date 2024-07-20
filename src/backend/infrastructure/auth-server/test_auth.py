import requests
from bs4 import BeautifulSoup
import base64

# 
# pip install requests beautifulsoup4
# 

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
  
# Define the endpoint URL
endpoint = "http://localhost:8080/oauth2/authorize?" \
  "response_type=code&" \
  "client_id=client&" \
  "scope=openid&" \
  "redirect_uri=my.redirect.uri"

# Create a session object to handle cookies
session = requests.Session()

# Make a GET request to the endpoint
print("Making a GET request to the endpoint")
response = session.get(endpoint, allow_redirects=True)

# Save the response content to a file
# with open('login_page.html', 'w') as file:
#     file.write(response.text)

# Extract the CSRF token from the login page
print("Extracting the CSRF token from the login page")
soup = BeautifulSoup(response.text, 'html.parser')
csrf_token = None
csrf_input = soup.find('input', {'name': '_csrf'})
if csrf_input:
    csrf_token = csrf_input.get('value')

print(f"CSRF token: {csrf_token}")

print("---------------------------------------------")

# Check the login page content to debug
# print("Login page content:")
# with open('login_page.html', 'r') as file:
#     print(file.read())

# Submit the login form
login_url = "http://localhost:8080/login"
login_data = {
    'username': 'ian',
    'password': 'pass',
    '_csrf': csrf_token
}

print("Submitting the login form")
login_response = session.post(login_url, data=login_data)

# Save the response content to a file
# with open('redirected_page.html', 'w') as file:
#     file.write(login_response.text)

# Print the final URL and status code of the response
print(f"Final URL after redirection: {login_response.url}")
print(f"Status code of the login response: {login_response.status_code}")

# Print the content of redirected page for debugging
# print("Redirected page content:")
# with open('redirected_page.html', 'r') as file:
#     print(file.read())

redirected_url_w_auth_code = login_response.url
code = extract_code_from_url(redirected_url_w_auth_code)
print(f"Extracted code: {code}")

# Exchange the authorization code for an access token
# Convert client secret to base64
http_basic_auth = 'client:secret'
http_basic_auth_base64 = base64.b64encode(http_basic_auth.encode()).decode()

# Make the POST request with the modified code
token_response = session.post("http://localhost:8080/oauth2/token", data={
  'grant_type': 'authorization_code',
  'code': code,
  'redirect_uri': 'my.redirect.uri',
  'client_id': 'client',
}, headers={'Authorization': f'Basic {http_basic_auth_base64}'})

print("Token response:")
print(token_response.json())