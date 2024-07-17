## E Commerce Microservice Application

### Technology Stack

#### Backend:
  - **Java21**
  - **Spring Boot and Spring Cloud ecosystem**

#### Infrastructure
  - **Database: MongoDB** - w/ Spring Data MongoDB
  - **Message Broker: Kafka**
  - **Reverse Proxy and Gateway: Nginx and ~~Netflix Eureka~~ Kubernetes Service + Nginx Ingress
  - **Authentication Server: Spring OAuth2 and Spring Security** **Not yet implemented*
  - **Monitoring and Logging** **Not yet implemented*

#### DevOps
  - **Containerization: Docker**
  - **Container Orchestration: Kubernetes**
    - Using Kind as the Cluster, Nginx Ingress as the Ingress Controller
  - **CI/CD: GitHub Actions**
    - test | build | push latest images to Dockerhub

#### FrontEnd
  - **TypeScript**
  - **Angular**
  - **RxJS**

#### System Design Patterns
  - **Reactive**
  - **Event Driven**
  - **Choreography - Saga**


