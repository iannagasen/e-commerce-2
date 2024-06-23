package dev.agasen.ecom.order.messaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.agasen.ecom.api.saga.order.events.OrderEvent;
import dev.agasen.ecom.api.saga.order.events.listener.OrderEventListener;
import dev.agasen.ecom.order.messaging.listener.DefaultOrderEventListener;
import reactor.core.publisher.Sinks;

@Configuration
public class OrderEventListenerConfig {
  
  @Bean
  public OrderEventListener orderEventListener(){
    var sink = Sinks.many().unicast().<OrderEvent>onBackpressureBuffer();
    var flux = sink.asFlux();
    return new DefaultOrderEventListener(sink, flux);
  }

}
