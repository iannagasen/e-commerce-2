package dev.agasen.ecom.payment;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(properties={
  "logging.level.root=ERROR",
  "logging.level.dev.agasen*=INFO",
  "spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest",
  "spring.cloud.stream.kafka.binder.brokers=localhost:9092"
})
public class BaseKafkaIntegrationTest extends MongoDBTestBase {
  
}