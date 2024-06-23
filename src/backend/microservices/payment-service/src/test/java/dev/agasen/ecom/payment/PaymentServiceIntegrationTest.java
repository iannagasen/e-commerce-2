package dev.agasen.ecom.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import dev.agasen.ecom.api.core.order.model.OrderItem;
import dev.agasen.ecom.api.core.payment.model.OrderPaymentRequest;
import dev.agasen.ecom.api.core.payment.model.TransactionType;
import dev.agasen.ecom.payment.persistence.BalanceEntity;
import dev.agasen.ecom.payment.persistence.BalanceRepository;
import dev.agasen.ecom.payment.persistence.PaymentEntity;
import dev.agasen.ecom.payment.persistence.PaymentRepository;
import dev.agasen.ecom.payment.service.PaymentProcessingService;
import reactor.test.StepVerifier;

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
public class PaymentServiceIntegrationTest extends MongoDBTestBase {
  
  @Autowired private PaymentRepository paymentRepo;
  @Autowired private BalanceRepository balanceRepo;
  @Autowired private PaymentProcessingService service;

  @BeforeEach
  void setup() {
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
  void testPaymentRequest() {
    var paymentReq = OrderPaymentRequest.builder()
      .orderId(100L)
      .customerId(2L)
      .orderItems(List.of(
        OrderItem.builder().productId(1L).price(75L).quantity(2).build(),
        OrderItem.builder().productId(2L).price(50L).quantity(1).build()
      ))
      .build();
  
    StepVerifier.create(service.processPayment(paymentReq))
      .assertNext(payment -> {
        assertNotNull(payment.getPaymentId());
        assertNotNull(payment.getTransactionDate());
        assertEquals(100L, payment.getOrderId(), 0);
        assertEquals(2L, payment.getCustomerId(), 0);
        assertEquals(2, payment.getOrderItems().size());
        assertEquals(TransactionType.PAYMENT, payment.getTransactionType());
      })
      .verifyComplete();

    StepVerifier.create(balanceRepo.findByCustomerId(2L))
      .assertNext(balance -> {
        assertEquals(2000L - 200L, balance.getAmount(), 0);
        assertEquals(0, 0);
      })
      .verifyComplete();
  }

  @Test
  void testPaymentInsufficientBalance() {
    var paymentReq = OrderPaymentRequest.builder()
      .orderId(100L)
      .customerId(2L)
      .orderItems(List.of(
        OrderItem.builder().productId(1L).price(3000L).quantity(2).build()
      ))
      .build();
  
    StepVerifier.create(service.processPayment(paymentReq))
      .expectErrorMatches(e -> 
          e instanceof RuntimeException &&
          e.getMessage().equals("Insufficient balance for customer: 2"))
      .verify();

    StepVerifier.create(balanceRepo.findByCustomerId(2L))
      .assertNext(balance -> {
        assertEquals(2000L, balance.getAmount(), 0);
      })
      .verifyComplete();
  }


  @Test
  void testRefund() {
    StepVerifier.create(service.refundPayment(1L))
      .assertNext(payment -> {
        assertNotNull(payment.getPaymentId());
        assertNotNull(payment.getTransactionDate());
        assertEquals(1L, payment.getOrderId(), 0);
        assertEquals(1L, payment.getCustomerId(), 0);
        assertEquals(2, payment.getOrderItems().size());
        assertEquals(TransactionType.REFUND, payment.getTransactionType());
      })
      .verifyComplete();

    StepVerifier.create(balanceRepo.findByCustomerId(1L))
      .assertNext(balance -> {
        assertEquals(1250L, balance.getAmount(), 0);
      })
      .verifyComplete();

    StepVerifier.create(paymentRepo.findAllByOrderId(1L).collectList())
      .assertNext(payments -> {
        assertEquals(2, payments.size());
        assertEquals(TransactionType.PAYMENT, payments.get(0).getTransactionType());
        assertEquals(TransactionType.REFUND, payments.get(1).getTransactionType());
      })
      .verifyComplete();
  }



  


}
