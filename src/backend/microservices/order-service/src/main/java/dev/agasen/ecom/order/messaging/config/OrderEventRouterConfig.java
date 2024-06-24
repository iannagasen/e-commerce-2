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
import dev.agasen.ecom.api.saga.order.processor.InventoryEventProcessor;
import dev.agasen.ecom.api.saga.order.processor.PaymentEventProcessor;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Configuration
@RequiredArgsConstructor
public class OrderEventRouterConfig extends AbstractOrderEventRouterConfig {

  private final EventPublisher<OrderEvent> eventPublisher;
  private final InventoryEventProcessor<OrderEvent> inventoryProcessor;
  private final PaymentEventProcessor<OrderEvent> paymentProcessor;

  @Bean
  public Function<Flux<Message<InventoryEvent>>, Flux<Message<OrderEvent>>> inventoryProcessor(){
    return super.processor(this.inventoryProcessor);
  }

  @Bean
  public Function<Flux<Message<PaymentEvent>>, Flux<Message<OrderEvent>>> paymentProcessor(){
    return super.processor(this.paymentProcessor);
  }

  @Bean
  public Supplier<Flux<Message<OrderEvent>>> orderEventProducer() {
      return () -> this.eventPublisher.publish()
                                      .map(super::toMessage);
  }

}
