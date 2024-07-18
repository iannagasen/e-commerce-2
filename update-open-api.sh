#! /bin/bash

# start the application in background
./gradlew src:backend:microservices:order-service:bootRun --args='--server.port=6666' -x test &
BOOT_RUN_PID=$!

# Wait for the server to start
echo "Waiting for the server to start..."
sleep 15

echo "Fetching the OpenAPI documentation..."
# curl http://localhost:6666/openapi/v3/api-docs.yaml -o ./openapi/api-docs.yaml
curl -H "Accept: */*" http://localhost:6666/openapi/v3/api-docs.yaml -o ./openapi/api-docs.yaml

echo "Stopping the Spring Boot application..."
sleep 3
kill $BOOT_RUN_PID