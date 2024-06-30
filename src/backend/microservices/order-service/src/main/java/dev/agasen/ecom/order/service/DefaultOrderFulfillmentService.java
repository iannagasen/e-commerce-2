package dev.agasen.ecom.order.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import dev.agasen.ecom.api.saga.order.status.OrderStatus;
import dev.agasen.ecom.order.messaging.service.OrderFulfillmentService;
import dev.agasen.ecom.order.persistence.OrderComponentEntity;
import dev.agasen.ecom.order.persistence.OrderComponentRepository;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import dev.agasen.ecom.order.persistence.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultOrderFulfillmentService implements OrderFulfillmentService {

  private final OrderComponentRepository repo;
  private final PurchaseOrderRepository purchaseOrderRepository;

  @Override
  public Mono<PurchaseOrderEntity> complete(Long orderId) {
    return repo.findAllByOrderId(orderId).collectList()
        .doOnNext(components -> {
          log.info("components - {}", components);
          components.forEach(component -> log.info("component - {}", component));
        })
        // check if all components are successful
        .filter(components -> components.stream().allMatch(OrderComponentEntity::isSuccessful))
        .flatMap(components -> purchaseOrderRepository.findByOrderId(orderId))
        .doOnNext(purchaseOrder -> purchaseOrder.setOrderStatus(OrderStatus.COMPLETED))
        .flatMap(purchaseOrderRepository::save)
        .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance));
  }

  @Override
  public Mono<PurchaseOrderEntity> cancel(Long orderId) {
    return purchaseOrderRepository.findByOrderId(orderId)
        .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
        .doOnNext(order -> order.setOrderStatus(OrderStatus.CANCELLED))
        .flatMap(purchaseOrderRepository::save)
        .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance));
  }
  
}
