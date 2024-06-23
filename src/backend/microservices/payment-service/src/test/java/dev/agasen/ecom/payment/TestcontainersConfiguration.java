package dev.agasen.ecom.payment;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {
	// COMMENTED OUT: Dont know how to use this approach yet (CODE WAS AUTO GENERATED)

	// @Bean
	// @ServiceConnection
	// KafkaContainer kafkaContainer() {
	// 	return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
	// }

	// @Bean
	// @ServiceConnection
	// MongoDBContainer mongoDbContainer() {
	// 	return new MongoDBContainer(DockerImageName.parse("mongo:6.0.4"));
	// }

}
