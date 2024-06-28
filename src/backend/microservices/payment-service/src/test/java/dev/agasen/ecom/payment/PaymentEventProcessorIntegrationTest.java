package dev.agasen.ecom.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.core.payment.model.TransactionType;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import dev.agasen.ecom.payment.persistence.BalanceEntity;
import dev.agasen.ecom.payment.persistence.BalanceRepository;
import dev.agasen.ecom.payment.persistence.PaymentEntity;
import dev.agasen.ecom.payment.persistence.PaymentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@TestPropertySource(properties = {
  // ! orderEventRouter = our application, orderEventProduucer & paymentEventConsumer = see TestConfiguration below
  "spring.cloud.function.definition=orderEventRouter;orderEventProducer;paymentEventConsumer",
  "spring.cloud.stream.bindings.orderEventProducer-out-0.destination=order-events",
  "spring.cloud.stream.bindings.paymentEventConsumer-in-0.destination=payment-events"
})
public class PaymentEventProcessorIntegrationTest extends BaseKafkaIntegrationTest {
  

  /**
   * ! Sink = producer/publisher that can be subscribe by 1 or more consumers
   * ! Sinks.many() - sink that can emit multiple items
   * ! unicast = single subscriber
   * ! onBackpressureBuffer = if sinks subscriber can't keep up with the incoming items, add buffer to handle backpressure
   */
  protected static final Sinks.Many<OrderEvent> requestSink    = Sinks.many().unicast().onBackpressureBuffer();
  protected static final Sinks.Many<PaymentEvent> responseSink = Sinks.many().unicast().onBackpressureBuffer();
  protected static final Flux<PaymentEvent> responseFlux = responseSink.asFlux().cache(0);

  @Autowired private PaymentRepository paymentRepo;
  @Autowired private BalanceRepository balanceRepo;

  @BeforeEach
  void setUp() {
    paymentRepo.deleteAll();
    balanceRepo.deleteAll();

    BalanceEntity customerBalance1 = BalanceEntity.builder()
      .balanceId(1L)
      .customerId(1L)
      .amount(1000L)
      .build();

    BalanceEntity customerBalance2 = BalanceEntity.builder()
      .balanceId(2L)
      .customerId(2L)
      .amount(2000L)
      .build();

    PaymentEntity payment1 = PaymentEntity.builder()
      .paymentId(1L)
      .orderId(1L)
      .customerId(1L)
      .orderItems(List.of(
        OrderItem.builder().productId(1L).price(100L).quantity(2).build(),
        OrderItem.builder().productId(2L).price(50L).quantity(1).build()
      ))
      .transactionType(TransactionType.PAYMENT)
      .build();

    paymentRepo.saveAll(List.of(payment1)).collectList().block();
    balanceRepo.saveAll(List.of(customerBalance1, customerBalance2)).collectList().block();
  }

  @Test
  void testPaymentEventProcessor() {
    var customerId = 1L;
    var orderCreatedEvent = OrderEvent.Created.builder()
      .orderId(1L)
      .customerId(customerId)
      .items(List.of(
          OrderItem.builder().productId(1L).price(100L).quantity(4).build(),
          OrderItem.builder().productId(2L).price(50L).quantity(3).build()
      ))
      .createdAt(Instant.now())
      .build();

  responseFlux
    .doFirst(() -> requestSink.tryEmitNext(orderCreatedEvent))
    .next().timeout(Duration.ofSeconds(5))
    .cast(PaymentEvent.Processed.class)
    .as(StepVerifier::create)
    .assertNext(processed -> {
      assertNotNull(processed);
      assertNotNull(processed.createdAt());
      assertEquals(1L, processed.orderId(), 0);
      assertEquals(customerId, processed.customerId(), 0);
      assertEquals(550L, processed.payment(), 0);

      var orderPayments = paymentRepo.findAllByOrderId(1L).collectList().block();
      assertEquals(1, orderPayments.size());
      var payment = orderPayments.get(0);
      assertEquals(2, payment.getOrderItems().size());

      var customerBalance = balanceRepo.findByCustomerId(customerId).block();
      assertEquals(customerBalance.getAmount(), 450L, 0);

    });

  }


  
  @TestConfiguration
  static class TestConfig {

    @Bean
    public Supplier<Flux<OrderEvent>> orderEventProducer(){
      return requestSink::asFlux;
    }

    @Bean
    public Consumer<Flux<PaymentEvent>> paymentEventConsumer(){
      return f -> f.doOnNext(responseSink::tryEmitNext).subscribe();
    }
  }
}
