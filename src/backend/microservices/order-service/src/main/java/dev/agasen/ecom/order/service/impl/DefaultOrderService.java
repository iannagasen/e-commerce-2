package dev.agasen.ecom.order.service.impl;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import dev.agasen.ecom.order.persistence.PurchaseOrderRepository;
import dev.agasen.ecom.order.service.OrderService;
import dev.agasen.ecom.util.mongo.SequenceGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderService implements OrderService {

  private final PurchaseOrderRepository purchaseOrderRepo;
  private final SequenceGeneratorService sequenceGenerator;

  @Override
  public Mono<PurchaseOrderEntity> placeOrder(CreateOrderRequest req) {
    return sequenceGenerator.generateSequence(PurchaseOrderEntity.SEQUENCE_NAME)
      .map(orderId -> PurchaseOrderEntity.fromPending(orderId, req.getCustomerId(), req.getItems()))
      .flatMap(purchaseOrderRepo::save)
      .doOnSuccess(order -> log.info("Order placed: {}", order));
  }
  
}
