package dev.agasen.ecom.order.service;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import dev.agasen.ecom.order.messaging.service.PaymentParticipantService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultPaymentParticipantService implements PaymentParticipantService {

  private final OrderComponentRepository repository;
  private final SequenceGeneratorService sequenceGenerator;

  @Override
  public Mono<Void> doOnSuccess(Long orderId) {
    return doIfNotYetProcessed(orderId, () -> sequenceGenerator.generateSequence(OrderComponentEntity.SEQUENCE_NAME)
      .map(id -> OrderComponentEntity.Payment.newSuccessful(id, ParticipantStatus.COMPLETED, orderId, null))
      .flatMap(repository::save)
    );
  }

  @Override
  public Mono<Void> doOnFailure(Long orderId) {
    return doIfNotYetProcessed(orderId, () -> sequenceGenerator.generateSequence(OrderComponentEntity.SEQUENCE_NAME)
      .map(id -> OrderComponentEntity.Payment.newUnsuccessful(id, ParticipantStatus.FAILED, orderId, null))
      .flatMap(repository::save)
    );
  }

  @Override
  public Mono<Void> doOnRollback(Long orderId) {
    return repository.findOrderPaymentByOrderId(orderId)
      .doOnNext(payment -> payment.setStatus(ParticipantStatus.ROLLBACK))
      .flatMap(repository::save)
      .then();
  } 

  private Mono<Void> doIfNotYetProcessed(Long orderId, Supplier<Mono<OrderComponentEntity.Payment>> supplier) {
    return repository.findOrderPaymentByOrderId(orderId)
        // make sure it was not processed yet
        // if it has already a component, then make sure to not process it to avoid duplication
        .switchIfEmpty(Mono.defer(supplier))
        .then();
  }

}
