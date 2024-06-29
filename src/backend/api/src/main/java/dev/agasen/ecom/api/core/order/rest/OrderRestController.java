package dev.agasen.ecom.api.core.order.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import reactor.core.publisher.Mono;

public interface OrderRestController {

  @PostMapping("/order")
  Mono<PurchaseOrder> placeOrder(@RequestBody Mono<CreateOrderRequest> req);

  @GetMapping("/order/inventory/{productId}")
  Mono<Inventory> getOrderInventory(@PathVariable(value = "productId") Long productId);

}
