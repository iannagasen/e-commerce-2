package dev.agasen.ecom.payment.messaging;

import dev.agasen.ecom.api.saga.order.events.OrderEvent.Cancelled;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Completted;
import dev.agasen.ecom.api.saga.order.events.OrderEvent.Created;

import java.time.Instant;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.payment.model.OrderPaymentRequest;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import dev.agasen.ecom.api.saga.order.processor.OrderEventProcessor;
import dev.agasen.ecom.payment.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentEventProcessor implements OrderEventProcessor<PaymentEvent> {

  private final PaymentProcessingService service;

  @Override
  public Mono<PaymentEvent> handle(Created event) {
    var req = OrderPaymentRequest.builder()
      .orderId(event.orderId())
      .customerId(event.customerId())
      .orderItems(event.items())
      .build();

    return service.processPayment(req)
      .doOnNext(payment -> log.info("Payment processed for Order: ", payment.getOrderId()))
      .map(payment -> PaymentEvent.Processed.builder()
        .orderId(payment.getOrderId())
        .customerId(payment.getCustomerId())
        .payment(payment.totalAmount())
        .createdAt(Instant.now())
        .build()
      );
  }
  

  @Override
  public Mono<PaymentEvent> handle(Cancelled event) {
    return service.refundPayment(event.orderId())
      .doOnNext(payment -> log.info("Payment refunded for Order: ", payment.getOrderId()))
      .map(payment -> PaymentEvent.Refunded.builder()
        .orderId(payment.getOrderId())
        .customerId(payment.getCustomerId())
        .amountRefunded(payment.totalAmount())
        .createdAt(Instant.now())
        .message(event.message())
        .build()
      );
  }

  @Override
  public Mono<PaymentEvent> handle(Completted event) {
    return Mono.empty();
  }

}
