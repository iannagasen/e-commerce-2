package dev.agasen.ecom.order;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.OrderDetails;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

  Mono<PurchaseOrderEntity> placeOrder(CreateOrderRequest req);

  Flux<OrderDetails> getOrders();
  
}