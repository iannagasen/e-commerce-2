#! /bin/bash

docker compose -f docker-compose-infra.yaml up -d

# start the application in background
./gradlew src:backend:microservices:order-service:bootRun --args='--server.port=6666' -x test &
BOOT_RUN_PID=$!

# Wait for the server to start
echo "Waiting for the server to start..."
sleep 15

echo "Fetching the OpenAPI documentation..."
# curl http://localhost:6666/openapi/v3/api-docs.yaml -o ./openapi/api-docs.yaml
curl -H "Accept: */*" http://localhost:6666/openapi/v3/api-docs.yaml -o ./openapi/orderservice-api-docs.yaml
echo "Done Fetching"

echo "Stopping the Spring Boot application..."
sleep 5
kill $BOOT_RUN_PID

find ./openapi/orderservice-api-docs.yaml -type f -exec sed -i 's/localhost:6666/localhost:8103/g' {} \;

### TODO: Consider replacing content: */* with application/json as the ng openapi generator does not support */*