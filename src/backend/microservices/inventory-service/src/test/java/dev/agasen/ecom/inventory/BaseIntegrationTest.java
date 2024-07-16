package dev.agasen.ecom.inventory;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@DirtiesContext
@SpringBootTest(properties={
  "logging.level.root=ERROR",
  "logging.level.dev.agasen*=INFO",
  "spring.cloud.stream.kafka.binder.configuration.auto.offset.reset=earliest",
  "spring.cloud.stream.kafka.binder.brokers=localhost:9092"
})
@EmbeddedKafka(
  partitions = 1,
  bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@TestPropertySource(properties = {
  // ! orderEventRouter = our application, orderEventProduucer & inventoryEventConsumer = see TestConfiguration below
  "spring.cloud.function.definition=orderEventRouter;orderEventProducer;inventoryEventConsumer",
  "spring.cloud.stream.bindings.orderEventProducer-out-0.destination=order-events",
  "spring.cloud.stream.bindings.inventoryEventConsumer-in-0.destination=inventory-events"
})
public abstract class BaseIntegrationTest extends MongoDBTestBase {
  
  /**
   * ! Sink = producer/publisher that can be subscribe by 1 or more consumers
   * ! Sinks.many() - sink that can emit multiple items
   * ! unicast = single subscriber
   * ! onBackpressureBuffer = if sinks subscriber can't keep up with the incoming items, add buffer to handle backpressure
   */
  protected static final Sinks.Many<OrderEvent> requestSink    = Sinks.many().unicast().onBackpressureBuffer();
  protected static final Sinks.Many<InventoryEvent> responseSink = Sinks.many().unicast().onBackpressureBuffer();
  protected static final Flux<InventoryEvent> responseFlux = responseSink.asFlux().cache(0);
  
  @TestConfiguration
  static class TestConfig {

    @Bean
    public Supplier<Flux<OrderEvent>> orderEventProducer(){
      return requestSink::asFlux;
    }

    @Bean
    public Consumer<Flux<InventoryEvent>> inventoryEventConsumer(){
      return f -> f.doOnNext(responseSink::tryEmitNext).subscribe();
    }
    
  }
}
