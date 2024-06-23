package dev.agasen.ecom.payment.messaging;

import java.util.function.Function;

import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;

import dev.agasen.ecom.api.messaging.record.MessageConverter;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import dev.agasen.ecom.api.saga.order.processor.OrderEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import org.springframework.messaging.Message;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderEventRouterConfig {
  
  private final OrderEventProcessor<PaymentEvent> processor;

  public Function<Flux<Message<OrderEvent>>, Flux<Message<PaymentEvent>>> orderEventRouter() {
    return orderEventFlux -> orderEventFlux
      .map(MessageConverter::toRecord)
      .doOnNext(orderEvent -> log.info("Received order event: {}", orderEvent))
      .concatMap(orderRecord -> processor
          .process(orderRecord.message())
          .doOnSuccess(paymentEvent -> log.info("Processed order event: {}", orderRecord))
      )
      .map(this::toMessage);
  }

  private Message<PaymentEvent> toMessage(PaymentEvent event) {
    return MessageBuilder.withPayload(event)
      .setHeader(KafkaHeaders.KEY, event.orderId().toString())
      .build();
  }

}
