package dev.agasen.ecom.api.messaging.processor;

import dev.agasen.ecom.api.messaging.DomainEvent;
import reactor.core.publisher.Mono;

public interface EventProcessor<T extends DomainEvent, R extends DomainEvent>{
  
  Mono<R> process(T event);

}