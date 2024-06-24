package dev.agasen.ecom.order.messaging.service;

import reactor.core.publisher.Mono;

public interface PaymentParticipantService {
  
  Mono<Void> doOnSuccess(Long orderId);
  Mono<Void> doOnFailure(Long orderId);
  Mono<Void> doOnRollback(Long orderId);

}
