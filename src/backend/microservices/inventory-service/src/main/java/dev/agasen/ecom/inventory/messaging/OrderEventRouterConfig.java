package dev.agasen.ecom.inventory.messaging;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import dev.agasen.ecom.api.messaging.record.MessageConverter;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class OrderEventRouterConfig {
  
  private final OrderInventoryEventProcessor processor;

  @Bean
  public Function<Flux<Message<OrderEvent>>, Flux<Message<InventoryEvent>>> orderEventRouter() {
    return orderEventFlux -> orderEventFlux
      .doFirst(() -> log.info("Received order events"))
      .map(MessageConverter::toRecord)
      .doOnNext(orderEvent -> log.info("Received order event: {}", orderEvent))
      .concatMap(orderRecord -> processor
          .process(orderRecord.message())
          .doOnSuccess(inventoryEvent -> log.info("Processed order event: {}", orderRecord))
      )
      .map(this::toMessage)
      .doOnError(e -> log.error("Error processing order event", e));
  }

  private Message<InventoryEvent> toMessage(InventoryEvent event) {
    return MessageBuilder.withPayload(event)
      .setHeader(KafkaHeaders.KEY, event.orderId().toString())
      .build();
  }

}
