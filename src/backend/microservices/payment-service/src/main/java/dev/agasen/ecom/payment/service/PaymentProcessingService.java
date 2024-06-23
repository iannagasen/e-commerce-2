package dev.agasen.ecom.payment.service;

import dev.agasen.ecom.api.core.payment.model.OrderPaymentRequest;
import dev.agasen.ecom.payment.persistence.PaymentEntity;
import reactor.core.publisher.Mono;

public interface PaymentProcessingService {

  Mono<PaymentEntity> processPayment(OrderPaymentRequest req);

  Mono<PaymentEntity> refundPayment(Long orderId);

}
