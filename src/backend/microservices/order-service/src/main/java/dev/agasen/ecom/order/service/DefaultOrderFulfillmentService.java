package dev.agasen.ecom.order.service;

import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.status.OrderStatus;
import dev.agasen.ecom.order.messaging.service.OrderFulfillmentService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import dev.agasen.ecom.order.persistence.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultOrderFulfillmentService implements OrderFulfillmentService {

  private final OrderComponentRepository repo;
  private final PurchaseOrderRepository purchaseOrderRepository;

  @Override
  public Mono<PurchaseOrderEntity> complete(Long orderId) {
    return repo.findAllByOrderId(orderId).collectList()
        .filter(components -> components.stream().allMatch(OrderComponentEntity::isSuccessful))
        .flatMap(components -> purchaseOrderRepository.findByOrderId(orderId))
        .doOnNext(purchaseOrder -> purchaseOrder.setOrderStatus(OrderStatus.COMPLETED))
        .flatMap(purchaseOrderRepository::save);
  }

  @Override
  public Mono<PurchaseOrderEntity> cancel(Long orderId) {
    return purchaseOrderRepository.findByOrderId(orderId)
        .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
        .doOnNext(order -> order.setOrderStatus(OrderStatus.CANCELLED))
        .flatMap(purchaseOrderRepository::save);
  }
  
}
