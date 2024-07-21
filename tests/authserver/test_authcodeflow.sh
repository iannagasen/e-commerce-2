#!/bin/bash

endpoint="http://localhost:8080/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=https://www.manning.com/authorized"

echo "Making a GET request to the endpoint"
# Make a GET request to the endpoint
#  -c: save cookies to file
#  -L: follow redirects
#  -X: specify request method
#  -o: save output to file
curl -c cookies.txt -L -X GET $endpoint -o login_page.html

# Extract the CSRF token from the login page
echo "Extracting the CSRF token from the login page"
csrf_token=$(cat login_page.html | grep csrf | grep -o 'value="[^"]*' | awk -F'"' '{print $2}')
echo "CSRF token: $csrf_token"


echo "---------------------------------------------"

# echo "Submitting the login form"
# # Explain
# #  -b: read cookies from file
# #  -c: save cookies to file
# #  -L: follow redirects
# #  -X: specify request method
# #  -d: data to send in the body of the request
# #  -o: save output to file
curl -b cookies.txt -c cookies.txt -v -L -X POST "http://localhost:8080/login" \
-H "Content-Type: application/x-www-form-urlencoded" \
-H "Referer: http://localhost:8080/login" \
-d "username=ian&password=pass&_csrf=$csrf_token" \
-o redirected_page.html -i

