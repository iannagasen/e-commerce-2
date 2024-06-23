package dev.agasen.ecom.order.messaging.config;

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import dev.agasen.ecom.api.messaging.publisher.EventPublisher;
import dev.agasen.ecom.api.saga.order.events.InventoryEvent;
import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.PaymentEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Configuration
@RequiredArgsConstructor
public class OrderEventRouterConfig extends AbstractOrderEventRouterConfig {

  private final EventPublisher<OrderEvent> eventPublisher;

  @Bean
  public Function<Flux<Message<InventoryEvent>>, Flux<Message<OrderEvent>>> inventoryProcessor(){
    return null;
  }

  @Bean
  public Function<Flux<Message<PaymentEvent>>, Flux<Message<OrderEvent>>> paymentProcessor(){
    return null;
  }

  @Bean
  public Supplier<Flux<Message<OrderEvent>>> orderEventProducer() {
      return () -> this.eventPublisher.publish()
                                      .map(super::toMessage);
  }

}
