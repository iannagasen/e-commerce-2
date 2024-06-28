package dev.agasen.ecom.order;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

// @AutoConfigureWebTestClient
@TestPropertySource(properties = {
  // we add the consumer on top of the 3 existing functions
  "spring.cloud.function.definition=orderEventProducer;paymentProcessor;inventoryProcessor;orderEventConsumer",
  // where are consuming the event
  "spring.cloud.stream.bindings.orderEventConsumer-in-0.destination=order-events",
})
public class OrderSagaIntegrationTest extends BaseKafkaIntegTest {

  // @Autowired WebTestClient client;
  @Autowired OrderService orderService;


  // what we are consuming
  static final Sinks.Many<OrderEvent> responseSink = Sinks.many().unicast().onBackpressureBuffer();
  static final Flux<OrderEvent> responseFlux = responseSink.asFlux().cache(0);

  static final CreateOrderRequest REQUEST = CreateOrderRequest.builder()
    .customerId(1L)
    .items(List.of(
      new OrderItem(1L, 1, 100L),
      new OrderItem(2L, 2, 200L),
      new OrderItem(3L, 3, 300L)

    ))
    .build();

  @Test
  void orderCompletedWorkflow() {
    var orderIdRef = new AtomicReference<Long>();
    
    orderService.placeOrder(REQUEST)
      .doOnNext(order -> orderIdRef.set(order.getOrderId()))
      .as(StepVerifier::create)
      .expectNextMatches(order -> order.getOrderId() != null)
      .verifyComplete();

    expectEvent(OrderEvent.Created.class, e -> {
      assert e.orderId().equals(orderIdRef.get());
      assert e.customerId().equals(REQUEST.getCustomerId());
      assert e.items().size() == REQUEST.getItems().size();
    });
  }

  private <T> void expectEvent(Class<T> type, Consumer<T> assertion) {
    responseFlux
      .next()
      .timeout(Duration.ofSeconds(5))
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
