#! /bin/bash

# Sample Usa
#   HOST=localhost PORT=7000 ./end_to_end_test.sh


: ${HOST=localhost}
: ${PORT=8080}
: ${ORDER_ID_CREATED_SUCCESSFULLY=1}

# Function: assert_curl
# Description: This function is used to send a curl request and assert the HTTP response code.
# Parameters:
#   - expected_http_code: The expected HTTP response code.
#   - curl_cmd: The curl command with options to send the request.
# Returns: None
function assert_curl() {
  local expected_http_code=$1
  
  # This variable stores the curl command with options to send a request and retrieve the HTTP response code.
  # The `-w` option is used to format the output and retrieve only the HTTP code.
  # The `-s` option is used to silence the progress meter and other unnecessary information.
  # The `-o /dev/null` option is used to discard the response body and save disk space.
  local curl_cmd="$2 -w \"%{http_code}\"";

  # This variable stores the result of the curl command.
  local result=$(eval $curl_cmd)

  # Extracts the last 3 characters from the variable 'result'
  local http_code="${result:(-3)}"

  # This line of code initializes the variable RESPONSE as an empty string.
  # It then checks if the length of the variable result is greater than 3.
  # If the condition is true, it assigns the value of result without the last three characters to RESPONSE.
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  # This code block checks the HTTP response code and performs actions based on the code.
  # If the HTTP response code matches the expected code, it checks if it is equal to 200.
  # If it is equal to 200, it prints "Test OK (HTTP code: $http_code)".
  # If it is not equal to 200, it prints "Test OK (HTTP code: $http_code) - $RESPONSE".
  # If the HTTP response code does not match the expected code, it prints "Test FAILED, EXPECTED HTTP CODE: $expected_http_code, GOT: $http_code, WILL ABORT!".
  # It also prints the failing command and the response.
  # Finally, it exits with code 1.
  if [ "$http_code" = "$expected_http_code" ]; 
  then
    if [ "$http_code" = "200" ]
    then
      echo "Test OK (HTTP code: $http_code)"
    else
      echo "Test OK (HTTP code: $http_code) - $RESPONSE"
    fi
  else
    echo "Test FAILED, EXPECTED HTTP CODE: $expected_http_code, GOT: $http_code, WILL ABORT!"
    echo "- Failing command: $curl_cmd"
    echo "- Response: $RESPONSE"
    exit 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]
  then
    echo "Test OK (actual value: $actual)"
  else
    echo "Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT"
    exit 1
  fi
}

function testOrderCreated() {
  local request_body=\
'{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 1
    }
  ]
}'
  local command="curl -X POST -H 'Content-Type: application/json' -d '$request_body' http://$HOST:$PORT/order"
  if ! assert_curl 200 "$command"
  then
    echo "Test FAILED, EXPECTED HTTP CODE: 200, GOT: $http_code, WILL ABORT!"
    return 1
  fi

  # This line disables the 'exit immediately if a command exits with a non-zero status' option.
  # It allows the script to continue executing even if a command fails.
  set +e
    assertEqual 1 $(echo $RESPONSE | jq .customerId)
    assertEqual 1 $(echo $RESPONSE | jq '.items | length')
    
    # -r option is used to remove the quotes from the output
    assertEqual "PENDING" $(echo $RESPONSE | jq -r '.orderStatus')
  set -e
}

set -e

echo "Start Test:" `date`
testOrderCreated