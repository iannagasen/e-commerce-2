package dev.agasen.ecom.order.rest;

import org.springframework.web.bind.annotation.RestController;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.rest.InventoryRestController;
import dev.agasen.ecom.api.core.inventory.rest.InventoryServiceProxy;
import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import dev.agasen.ecom.api.core.order.rest.OrderRestController;
import dev.agasen.ecom.order.OrderService;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderRestController {
  
  private final OrderService orderService;
  private final InventoryServiceProxy inventoryServiceProxy;

  public Mono<PurchaseOrder> placeOrder(Mono<CreateOrderRequest> req) {
      return req.flatMap(orderService::placeOrder)
        .map(PurchaseOrderEntity::toRestModel)
        .doFirst(() -> System.out.println("RECEIVVVVVVEEEED!!!!!"))
        .doOnError(order -> System.out.println("ERROR!!! " + order));
  }

  public Mono<Inventory> getOrderInventory(Long productId) {
    return inventoryServiceProxy.getInventory(productId);
  }
  
  
  

}
