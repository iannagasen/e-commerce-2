package dev.agasen.ecom.order.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
  public Mono<PurchaseOrderEntity> complete(Long orderId) {
    return repo.findAllByOrderId(orderId).collectList()
        // check if all components are successful
        .filter(components -> components.stream().allMatch(OrderComponentEntity::isSuccessfulAndProcessed))
        .doOnNext(components -> log.info("All components are successful for order {}, {}", orderId, components))

        .map(components -> components.stream().map(OrderComponentEntity::setComplete).toList())
        .flatMap(components -> repo.saveAll(components).collectList())
        .doOnNext(components -> log.info("All components are saved for order {}, {}", orderId, components))

        .zipWith(purchaseOrderRepository.findByOrderId(orderId), (components, order) -> order)
        .doOnNext(order -> log.info("Order {} is found", order))

        .map(purchaseOrder -> purchaseOrder.setOrderStatus(OrderStatus.COMPLETED))
        .flatMap(purchaseOrderRepository::save)
        .doOnNext(order -> log.info("Order {} is saved", order))


        .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance))
        .doOnSuccess(order -> log.info("Order {} is completed", order));
  }

  @Override
  @Transactional
  public Mono<PurchaseOrderEntity> cancel(Long orderId) {
    return purchaseOrderRepository.findByOrderId(orderId)
        .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
        .doOnNext(order -> order.setOrderStatus(OrderStatus.CANCELLED))
        .flatMap(purchaseOrderRepository::save)
        .retryWhen(Retry.max(1).filter(OptimisticLockingFailureException.class::isInstance));
  }
  
}
