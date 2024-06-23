package dev.agasen.ecom.api.messaging.publisher;

import reactor.core.publisher.Flux;

public interface EventPublisher<T> {

  Flux<T> publish();

}
