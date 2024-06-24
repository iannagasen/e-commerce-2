package dev.agasen.ecom.order.messaging.service;

import reactor.core.publisher.Mono;

public interface InventoryParticipantService {

  Mono<Void> doOnSuccess(Long orderId);
  Mono<Void> doOnFailure(Long orderId);
  Mono<Void> doOnRollback(Long orderId);
  
}
