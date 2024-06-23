package dev.agasen.ecom.payment;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;

public class MongoDBTestBase {
  private static final String MONGODB_VER = "mongo:6.0.4";
  protected static final MongoDBContainer MONGODB = new MongoDBContainer(MONGODB_VER);

  static {
    MONGODB.start();
  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.host", MONGODB::getContainerIpAddress);
    registry.add("spring.data.mongodb.port", () -> MONGODB.getMappedPort(27017));
    registry.add("spring.data.mongodb.database", () -> "payment-test");
  }  
}
