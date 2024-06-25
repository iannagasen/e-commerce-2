package dev.agasen.ecom.order.messaging.config;

import java.util.function.Function;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import dev.agasen.ecom.api.messaging.DomainEvent;
import dev.agasen.ecom.api.messaging.processor.EventProcessor;
import dev.agasen.ecom.api.messaging.record.MessageConverter;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public abstract class AbstractOrderEventRouterConfig {

  private static final String DESTINATION_HEADER = "spring.cloud.stream.sendto.destination";
  private static final String ORDER_EVENTS_CHANNEL = "order-events-channel";

  protected <T extends DomainEvent> Function<Flux<Message<T>>, Flux<Message<OrderEvent>>> processor(EventProcessor<T, OrderEvent> processor) {
    return flux -> flux
        .map(MessageConverter::toRecord)
        .doOnNext(record -> log.info("Received message: {}", record))
        .concatMap(record -> processor.process(record.message())
                                      .doOnSuccess(event -> record.acknowledgement().acknowledge()))
        .map(this::toMessage);
  }

  protected Message<OrderEvent> toMessage(OrderEvent event) {
    log.info("order service produced {}", event);
    return MessageBuilder.withPayload(event)
                         .setHeader(KafkaHeaders.KEY, event.orderId().toString())
                         .setHeader(DESTINATION_HEADER, ORDER_EVENTS_CHANNEL)
                         .build();
}
}
