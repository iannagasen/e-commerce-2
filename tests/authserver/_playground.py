import requests
import json
import base64

_session = requests.Session()

token = 'eyJraWQiOiI1YTk1MmNmNi1lMGU5LTQxMWYtYjU0OS05NzgxMTNkODA5ZWYiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJpYW4iLCJhdWQiOiJhbmd1bGFyX2NsaWVudCIsIm5iZiI6MTcyMjA0MjM3OCwic2NvcGUiOlsid3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwicHJvcGVydHkiOiJISUdIIiwiZXhwIjoxNzIyMDQyNjc4LCJpYXQiOjE3MjIwNDIzNzgsImp0aSI6IjM4N2NjYmUzLTBmYTgtNDg3ZC1iZTRkLTk3YWIyNjZjNGUzZiJ9.R6c_JMXXHLXBt878mMQrmPAr4Rj2pP7bJyTNUepdib_Ld23uex5XymoxyuOqF4KXYFhA91jlwCiVt5E8U9EwEHyD-abfIzWIuSGKrCiUOKdtjW1cnmCVP0-NFXb6YpxJLlAOaRwzYkroQ6V50Jd8JHVCqLbC9nNDqrlpBjMXRO6oOZSDh3pKiVwEJTPu_xr4yc6ZVv1n2BBuoRgEs7hJXFtHjrHv_e09JG79kgXTePR7rP6FrXwwGM9iCthh40wvievkDkD1QWEkuBIgkM2n8PZuOBr4pI4uJDbw1vqYwcRRnrSESoypLKWj9frF1S6j1G7d0REmFwS9N51ZyNvotg'
client_id = 'order_service_client'
client_secret = 'order_service_client_secret'
_http_basic_auth = base64.b64encode(f"{client_id}:{client_secret}".encode()).decode()
response = _session.post("http://localhost:8080/oauth2/introspect", 
  data={'token': token}, 
  headers={'Authorization': f'Basic {_http_basic_auth}'})

print(json.dumps(response.json(), indent=4))


# get user details
#############################
# token = 'eyJraWQiOiI0MThiOWRmMS0wZThjLTQ0MGItOTdkNi01OTVhMzFiNzEyZDAiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJpYW4iLCJhdWQiOiJhbmd1bGFyX2NsaWVudCIsIm5iZiI6MTcyMjAzOTM1Miwic2NvcGUiOlsid3JpdGUiXSwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwicHJvcGVydHkiOiJISUdIIiwiZXhwIjoxNzIyMDM5NjUyLCJpYXQiOjE3MjIwMzkzNTIsImp0aSI6IjA4OWIxMWZiLTcwNTktNGFjNS04MTdkLTQ5OWI1NmJmNzk5ZCJ9.tBLaPWfqdFT-7kGW_0sbTdV0_N2WBK9IJL26KWjhM1AsBOvBbVrTnAXkZ_2KZcb61o8VGLcZGbAfMLVi9PMhE4RO8Dejb5wSUjN0pWSlbbXNbkw0OONpfgt22RoGnbCLe-Pa_Hz5QGYuJRUI4VlXgEXJmS1_EZLeDMN5bXMn8FZ_SSF3reqk6Dh6JZreX9PQbKzfOHO0wlDbwW0L4qPgOFYu_mkfwqk5AWY4l00M1KiKBHX9bgdvilw4MSwZqA-x4ecQG1TZuYS2XYE5fl5Hhe3aAj40jYgZTd7dPbu-KB_cobIijD7vFhL0IzYlD6P2Knde6Nusw-y3iTQqVMBZOA'
# client_id = 'angular_client'
# client_secret = 'angular_client_secret'
# _http_basic_auth = base64.b64encode(f"{client_id}:{client_secret}".encode()).decode()
response = _session.post("http://localhost:8080/userinfo?access_token=" + token, 
  headers={'Authorization': f'Basic {_http_basic_auth}'})

# # response = _session.post("http://localhost:8080/userinfo?access_token=" + token,) 


# # print(json.dumps(response.json(), indent=4))
if response.status_code == 200:
    user_info = response.text
    print("User Info:", user_info)
else:
    print("Failed to get user info:", response.status_code, response.text)