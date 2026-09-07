import requests
import json
script_url = "https://script.google.com/macros/s/AKfycbwYzxNzpfcUcdvChf8yh8sOQnhRJfMmTQ3kIT4e0X6xqv7BfOIRMRBXsfvFrzbplMXK4Q/exec"
payload = {
    "to": "vitconcuaminhtran@gmail.com",
    "subject": "Test from Script",
    "body": "It works!"
}
resp = requests.post(script_url, json=payload, timeout=15)
print(resp.status_code, resp.text)
