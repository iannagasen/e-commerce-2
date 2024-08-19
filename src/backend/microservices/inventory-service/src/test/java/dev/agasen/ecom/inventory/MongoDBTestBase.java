package dev.agasen.ecom.inventory;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

public abstract class MongoDBTestBase {
  private static final String MONGODB_VER = "mongo:6.0.4";
  
  @Container
  protected static GenericContainer<?> MONGODB = new GenericContainer<>("mongo:6.0.4")
  .withEnv("MONGO_INITDB_ROOT_USERNAME", "admin")
  .withEnv("MONGO_INITDB_ROOT_PASSWORD", "password")
  .withEnv("MONGO_INITDB_DATABASE", "inventory-db")
  .waitingFor(Wait.forLogMessage("(?i).*Waiting for connections*.*", 1))
  .withExposedPorts(27017); 

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    MONGODB.start();
    registry.add("spring.data.mongodb.host", MONGODB::getHost);
    registry.add("spring.data.mongodb.port", MONGODB::getFirstMappedPort);
  }  
}
