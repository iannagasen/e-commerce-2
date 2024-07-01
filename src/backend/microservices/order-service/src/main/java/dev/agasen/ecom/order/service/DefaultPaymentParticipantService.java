package dev.agasen.ecom.order.service;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.status.ParticipantStatus;
import dev.agasen.ecom.order.messaging.service.PaymentParticipantService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentEntity.Payment;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPaymentParticipantService implements PaymentParticipantService {

  private final OrderComponentRepository repository;

  @Override
  public Mono<Void> doOnSuccess(Long orderId) {
    return doIfNotYetProcessed(orderId, Payment::setProcessingSuccess);
  }

  @Override
  public Mono<Void> doOnFailure(Long orderId) {
    return doIfNotYetProcessed(orderId, pmt -> {
      pmt.setProcessingFailed("Payment participant failed to process");
    });
  }

  @Override
  public Mono<Void> doOnRollback(Long orderId) {
    return repository.findOrderPaymentByOrderId(orderId)
      .doOnNext(payment -> payment.setStatus(ParticipantStatus.ROLLBACK))
      .flatMap(repository::save)
      .then();
  } 

  private Mono<Void> doIfNotYetProcessed(Long orderId, Consumer<OrderComponentEntity.Payment> mapper) {
    return repository.findOrderPaymentByOrderId(orderId)
        .filter(inv -> inv.getStatus() == ParticipantStatus.PENDING)
        .doOnNext(inv -> log.info("Processing inventory component: {}", inv))
        .doOnNext(mapper)
        .flatMap(repository::save)
        .then();
  }

}
