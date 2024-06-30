package dev.agasen.ecom.order.service;

import java.util.function.Consumer;
import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import dev.agasen.ecom.order.messaging.service.InventoryParticipantService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultInventoryParticipantService implements InventoryParticipantService {

  private final OrderComponentRepository repository;

  @Override
  public Mono<Void> doOnSuccess(Long orderId) {
    return doIfNotYetProcessed(orderId, OrderComponentEntity.Inventory::setCompletedAndSuccessful);
  }

  @Override
  public Mono<Void> doOnFailure(Long orderId) {
    return doIfNotYetProcessed(orderId, inv -> inv.setComplettedButUnsuccessful(""));
  }

  @Override
  public Mono<Void> doOnRollback(Long orderId) {
    return repository.findOrderInventoryByOrderId(orderId)
      .doOnNext(inv -> inv.setStatus(ParticipantStatus.ROLLBACK))
      .flatMap(repository::save)
      .then();
  }
  
  private Mono<Void> doIfNotYetProcessed(Long orderId, Consumer<OrderComponentEntity.Inventory> mapper) {
    return repository.findOrderInventoryByOrderId(orderId)
        // throw exception if empty since, a Pending Inventory component should always be present at initiation of order
        .switchIfEmpty(Mono.error(new IllegalArgumentException("No inventory component found for order: " + orderId)))
        .filter(inv -> inv.getStatus() == ParticipantStatus.PENDING)
        .cast(OrderComponentEntity.Inventory.class)
        .doOnNext(mapper)
        .flatMap(repository::save)
        .then();
  }
  
}
