package dev.agasen.ecom.order;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import dev.agasen.ecom.order.persistence.OrderComponentEntity.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

// @AutoConfigureWebTestClient
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
  // we add the consumer on top of the 3 existing functions
  "spring.cloud.function.definition=orderEventProducer;paymentProcessor;inventoryProcessor;orderEventConsumer",
  // where are consuming the event
  "spring.cloud.stream.bindings.orderEventConsumer-in-0.destination=order-events",
})
@Import(BaseKafkaIntegTest.TestConfig.class)
public class BaseKafkaIntegTest extends BaseMongoDBIntegTest {

  // @Autowired WebTestClient client;
  @Autowired OrderService orderService;
  @Autowired StreamBridge streamBridge;

  @Autowired OrderComponentRepository orderComponentRepository;

  // what we are consuming
  static final Sinks.Many<OrderEvent> responseSink = Sinks.many().unicast().onBackpressureBuffer();
  static final Flux<OrderEvent> responseFlux = responseSink.asFlux().cache(0);  

  protected Long intiateOrder(CreateOrderRequest request) {
    AtomicReference<Long> orderIdRef = new AtomicReference<>();
    orderService.placeOrder(request)
      .doOnNext(order -> orderIdRef.set(order.getOrderId()))
      .as(StepVerifier::create)
      .expectNextMatches(order -> order.getOrderId() != null)
      .verifyComplete();
    return orderIdRef.get();
  }

  protected void verifyOrderCreatedEvent(Long orderId, Consumer<OrderEvent.Created> assertion) {
    expectEvent(OrderEvent.Created.class, e -> {
      assert e.orderId().equals(orderId);
      assertion.accept(e);
    });
  }

  protected void verifyOrderComponentsInPendingState(Long orderId) {
    StepVerifier.create(orderComponentRepository.findAllByOrderId(orderId).collectList())
      .consumeNextWith(components -> {
        assert components.size() == 2;
        assert components.stream().anyMatch(OrderComponentEntity.Inventory.class::isInstance);
        assert components.stream().anyMatch(OrderComponentEntity.Payment.class::isInstance);
        assert components.stream().map(OrderComponentEntity::getStatus).allMatch(ParticipantStatus.PENDING::equals);
      })
      .verifyComplete();
  }

  protected void verifyOrderComponentsInCompletedState(Long orderId) {
    StepVerifier.create(orderComponentRepository.findAllByOrderId(orderId).collectList())
      .consumeNextWith(components -> {
        assert components.size() == 2;
        assert components.stream().anyMatch(OrderComponentEntity.Inventory.class::isInstance);
        assert components.stream().anyMatch(OrderComponentEntity.Payment.class::isInstance);
        assert components.stream().map(OrderComponentEntity::getStatus).allMatch(ParticipantStatus.COMPLETED::equals);
      })
      .verifyComplete();
  }

  protected void verifyOrderCancelledEvent(Long orderId) {
    expectEvent(OrderEvent.Cancelled.class, e -> {
      assert e.orderId().equals(orderId);
    });
  }
  
  protected void verifyOrderComplettedEvent(Long orderId) {
    expectEvent(OrderEvent.Completted.class, e -> {
      assert e.orderId().equals(orderId);
    });
  }

  protected void emitEvent(PaymentEvent event) {
    streamBridge.send("payment-events", event);
  }

  protected void emitEvent(InventoryEvent event) {
    streamBridge.send("inventory-events", event);
  }

  protected <T> void expectEvent(Class<T> type, Consumer<T> assertion) {
    responseFlux
      .next()
      .timeout(Duration.ofSeconds(5), Mono.empty())
      .cast(type)
      .as(StepVerifier::create)
      .consumeNextWith(assertion)
      .verifyComplete();
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    public Consumer<Flux<OrderEvent>> orderEventConsumer(){
      return f -> f.doOnNext(responseSink::tryEmitNext).subscribe();
    }
  }

  
}
