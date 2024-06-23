package dev.agasen.ecom.payment.service.impl;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.payment.model.OrderPaymentRequest;
import dev.agasen.ecom.payment.persistence.BalanceRepository;
import dev.agasen.ecom.payment.persistence.PaymentEntity;
import dev.agasen.ecom.payment.persistence.PaymentRepository;
import dev.agasen.ecom.payment.service.PaymentProcessingService;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultPaymentProcessingService implements PaymentProcessingService {

  private final PaymentRepository paymentRepo;
  private final BalanceRepository balanceRepo;
  private final SequenceGeneratorService sequenceGenerator;

  @Override
  public Mono<PaymentEntity> processPayment(OrderPaymentRequest req) {
    return balanceRepo.findByCustomerId(req.getCustomerId())
      .switchIfEmpty(Mono.error(new RuntimeException("Balance not found for customer: " + req.getCustomerId())))
      .filter(balance -> balance.getAmount() >= req.totalAmount())
      .switchIfEmpty(Mono.error(new RuntimeException("Insufficient balance for customer: " + req.getCustomerId())))
      .doOnNext(balance -> balance.deduct(req.totalAmount()))
      .flatMap(balance -> balanceRepo.save(balance))
      .zipWith(sequenceGenerator.generateSequence(PaymentEntity.SEQUENCE_NAME)
          .map(paymentId -> PaymentEntity.newPayment(paymentId, req.getOrderId(), req.getCustomerId(), req.getOrderItems()))
          .flatMap(paymentRepo::save),
          (balance, payment) -> payment
      );
  }

  @Override
  public Mono<PaymentEntity> refundPayment(Long orderId) {
    throw new UnsupportedOperationException("Unimplemented method 'refundPayment'");
  }
  
}
