### WORKFLOWS

#### HappyPath
- **OrderCreated** 
  - **PaymentDeducted** 
    - **OrderCompletted**
  - **InventoryDeducted**
    - **OrderCompletted**
    - 
#### With Issue on Payment Service
- OrderCreated
  - PaymentDeclined
    - OrderCancelled
      - PaymentRefunded
        - OrderCompletted
      - InventoryRefunded
        - OrderCompletted
  - ***InventoryDeducted or InventoryDeclined - doesnt matter since it will be cancelled***

#### With Issue on Inventory Service
- OrderCreated
  - InventoryDeclined
    - OrderCancelled
      - InventoryRefunded
        - OrderCompletted
      - PaymentRefunded
        - OrderCompletted
  - ***PaymentDeducted or PaymentDeclined - doesnt matter since it will be cancelled***



URLS:
  - EurekaDiscoveryServer - http://localhost:8761

ERRORS:
  `MessageDeliveryException ... doesn't have subscribers to accept message`
    - Error during runtime/processing
    - usually runtime exception
    - or misconfiguration

- If you supply a Mono using `.map()` it will not trigger that Mono
  - always use `.flatMap()` when trying to sequencially trigger another Mono


### Kafka issue on Kubernetes
RESOURCE WITH **ERROR**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka-deployment
spec:
  selector:
    matchLabels:
      app: kafka
  replicas: 1
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: bitnami/kafka:3.3.2
        env:
        - name: KAFKA_ENABLE_KRAFT
          value: "yes"
        - name: KAFKA_CFG_PROCESS_ROLES
          value: "broker,controller"
        - name: KAFKA_CFG_CONTROLLER_LISTENER_NAMES
          value: "CONTROLLER"
        - name: KAFKA_CFG_LISTENERS
          value: "PLAINTEXT://:9092,CONTROLLER://:2181,EXTERNAL://:9094"
        - name: KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP
          value: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT"
        - name: KAFKA_CFG_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094"
        - name: KAFKA_BROKER_ID
          value: "1"
        - name: KAFKA_CFG_CONTROLLER_QUORUM_VOTERS
          value: "1@kafka:2181"
        - name: ALLOW_PLAINTEXT_LISTENER
          value: "yes"
        - name: KAFKA_CFG_NODE_ID
          value: "1"
        - name: KAFKA_KRAFT_CLUSTER_ID
          value: "MkU3OEVBNTcwNTJENDM2Qk"
        ports:
        # - containerPort: 9092
        - containerPort: 9094

---
apiVersion: v1
kind: Service
metadata:
  name: kafka
spec:
  selector:
    app: kafka
  ports:
  # - name: kafka-internal
  #   port: 9092
  #   targetPort: 9092
  - name: kafka-external
    port: 9094
    targetPort: 9094
```

**RESOLUTION**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kafka-deployment
spec:
  selector:
    matchLabels:
      app: kafka
  replicas: 1
  template:
    metadata:
      labels:
        app: kafka
    spec:
      containers:
      - name: kafka
        image: bitnami/kafka:3.3.2
        env:
        - name: KAFKA_ENABLE_KRAFT
          value: "yes"
        - name: KAFKA_CFG_PROCESS_ROLES
          value: "broker,controller"
        - name: KAFKA_CFG_CONTROLLER_LISTENER_NAMES
          value: "CONTROLLER"
        - name: KAFKA_CFG_LISTENERS
          value: "PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094"  # Changed CONTROLLER port to 9093
        - name: KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP
          value: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT"
        - name: KAFKA_CFG_ADVERTISED_LISTENERS
          value: "PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094"  # Ensure this matches the listeners
        - name: KAFKA_BROKER_ID
          value: "1"
        - name: KAFKA_CFG_CONTROLLER_QUORUM_VOTERS
          value: "1@kafka:9093"  # Ensure this matches the CONTROLLER listener
        - name: ALLOW_PLAINTEXT_LISTENER
          value: "yes"
        - name: KAFKA_CFG_NODE_ID
          value: "1"
        - name: KAFKA_KRAFT_CLUSTER_ID
          value: "MkU3OEVBNTcwNTJENDM2Qk"
        ports:
        - containerPort: 9092  # Add internal port for internal communication
        - containerPort: 9093  # Add port for CONTROLLER
        - containerPort: 9094  # External access port

---
apiVersion: v1
kind: Service
metadata:
  name: kafka
spec:
  selector:
    app: kafka
  ports:
  - name: kafka-internal
    port: 9092
    targetPort: 9092
  - name: kafka-controller
    port: 9093
    targetPort: 9093
  - name: kafka-external
    port: 9094
    targetPort: 9094
```


https://chatgpt.com/c/82ecf998-92ad-4e34-8cac-2009e516093a

1. Incorrect Listener Configuration:

Ensure the listeners and advertised listeners are correctly configured. The CONTROLLER listener should not be conflated with the old Zookeeper port (2181). It should be unique and not reused from Zookeeper configurations.
The KAFKA_CFG_LISTENERS should align with the advertised listeners.
Service Configuration:

2. Verify that your service configuration exposes all necessary ports and maps them correctly.
Cluster ID Configuration:
****
3. Ensure the cluster ID is consistent across your Kafka nodes if you are planning to run a multi-node setup.
Network Policies and DNS:

4. Check if the Kafka broker can resolve its own service name (kafka) and reach it correctly.