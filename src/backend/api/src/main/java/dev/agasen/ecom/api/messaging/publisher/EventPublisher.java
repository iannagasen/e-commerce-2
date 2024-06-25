package dev.agasen.ecom.api.messaging.publisher;

import dev.agasen.ecom.api.messaging.DomainEvent;
import reactor.core.publisher.Flux;

public interface EventPublisher<T extends DomainEvent> {

  Flux<T> publish();

}
