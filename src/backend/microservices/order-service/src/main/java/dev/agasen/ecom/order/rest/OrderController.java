package dev.agasen.ecom.order.rest;

import java.util.List;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.inventory.rest.InventoryServiceProxy;
import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.GetOrdersType;
import dev.agasen.ecom.api.core.order.model.OrderDetails;
import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import dev.agasen.ecom.api.core.order.rest.OrderRestController;
import dev.agasen.ecom.order.OrderService;
import dev.agasen.ecom.order.persistence.PurchaseOrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@Slf4j
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

  @Override
  public Mono<List<PurchaseOrder>> getOrders(@AuthenticationPrincipal Jwt jwt,  GetOrdersType type) {
    log.info("Jwt is {}", jwt);
    return orderService.getOrders(type)
      .map(PurchaseOrderEntity::toRestModel)
      .collectList();
  }

}
