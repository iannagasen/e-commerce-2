import requests
import json
import base64

_session = requests.Session()

token = 'eyJraWQiOiI3ZmVlZGZmNy02NjdlLTRmYWMtODdiNy03ZGU3MzBjZTBjYTEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1IiwiYXVkIjoid3JpdGVyIiwibmJmIjoxNzA0ODgwMjY4LCJzY29wZSI6WyJxdWl6OndyaXRlIl0sImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6OTk5OSIsImV4cCI6MTcwNDg4Mzg2OCwiaWF0IjoxNzA0ODgwMjY4LCJqdGkiOiI4MWM1OTIzNy0xYjBjLTRmNWEtOGU1Ni01Y2I2YjA2ZTQ5ZDQifQ.vVObsyw62YALF2EmSpFEwNuvLL9X30nw8kmXiScMxZgFfD4kKSfv2QMOHfV_0_hDbgVHNQwdZoi5DEUcyd-sHJpvFjAflpWXRFF5MrvOgBoMKVx4J9SSLg8FPW4H-VS1zcIpW1NsQ3ETWx5Ui3QhQWmcItT1H4ow6SjySW4dAPszp7tcvOf8oOmbFPthQVSLfYGNhatMMuetOh9bmhdotaNmbVXT7kMWcRN0Vio8lVGcOoY5demjgr-wW56lxZimZ0KvaoRcvxlZky06hmEG2O30uFIzrzhbY9uqyiv41pzC55L-g3eJjd8FB-jvDTd8vfwoYIuMiLb0Ucs-IWXKKA'
client_id = 'client'
client_secret = 'secret'
_http_basic_auth = base64.b64encode(f"{client_id}:{client_secret}".encode()).decode()
response = _session.post("http://localhost:8080/oauth2/introspect", 
  data={'token': token}, 
  headers={'Authorization': f'Basic {_http_basic_auth}'})

print(json.dumps(response.json(), indent=4))