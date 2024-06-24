package dev.agasen.ecom.order.service;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import dev.agasen.ecom.order.messaging.service.InventoryParticipantService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultInventoryParticipantService implements InventoryParticipantService {

  private final OrderComponentRepository repository;
  private final SequenceGeneratorService sequenceGenerator;

  @Override
  public Mono<Void> doOnSuccess(Long orderId) {
    return doIfNotYetProcessed(orderId, () -> sequenceGenerator.generateSequence(OrderComponentEntity.SEQUENCE_NAME)
      .map(id -> OrderComponentEntity.Inventory.newSuccessful(id, ParticipantStatus.COMPLETED, orderId, null))
      .flatMap(repository::save)
    );
  }

  @Override
  public Mono<Void> doOnFailure(Long orderId) {
    return doIfNotYetProcessed(orderId, () -> sequenceGenerator.generateSequence(OrderComponentEntity.SEQUENCE_NAME)
      .map(id -> OrderComponentEntity.Inventory.newUnsuccessful(id, ParticipantStatus.COMPLETED, orderId, null))
      .flatMap(repository::save)
    );
  }

  @Override
  public Mono<Void> doOnRollback(Long orderId) {
    return repository.findOrderInventoryByOrderId(orderId)
      .doOnNext(inv -> inv.setStatus(ParticipantStatus.ROLLBACK))
      .flatMap(repository::save)
      .then();
  }

  private Mono<Void> doIfNotYetProcessed(Long orderId, Supplier<Mono<OrderComponentEntity.Inventory>> supplier) {
    return repository.findOrderInventoryByOrderId(orderId)
        // make sure it was not processed yet
        // if it has already a component, then make sure to not process it to avoid duplication
        .switchIfEmpty(Mono.defer(supplier))
        .then();
  }
  
}
