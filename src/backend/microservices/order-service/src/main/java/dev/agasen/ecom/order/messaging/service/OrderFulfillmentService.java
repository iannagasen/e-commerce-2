package dev.agasen.ecom.order.messaging.service;

import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import reactor.core.publisher.Mono;

public interface OrderFulfillmentService {

  Mono<PurchaseOrderEntity> complete(Long orderId);
  
  Mono<PurchaseOrderEntity> cancel(Long orderId);

}
