package dev.agasen.ecom.util.event;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class DuplicateEventValidator {
  
  // public Mono<Void> emitErrorForRedundantProcessing(Mono<Boolean> mono) {
  //   return mono
  //       .flatMap(b -> b ? Mono.error(new EventAlreadyProcessedException()) : Mono.empty())
  //       .doOnError(EventAlreadyProcessedException.class, ex -> log.warn("Duplicate Event"))
  //       .then();
  // }

  // public <T> Mono<T> validate(Mono<Boolean> eventValidationPublisher, Mono<T> eventProcessingPublisher) {
  //   return eventValidationPublisher
  //       .transform(this::emitErrorForRedundantProcessing)
  //       .then(eventProcessingPublisher);

  // }
}
