package dev.agasen.ecom.api.core.order.rest;

import java.util.List;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import dev.agasen.ecom.api.core.inventory.model.Inventory;
import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.GetOrdersType;
import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import reactor.core.publisher.Mono;

public interface OrderRestController {


  @PostMapping(value = "/order", produces = "application/json")
  Mono<PurchaseOrder> placeOrder(@RequestBody Mono<CreateOrderRequest> req);

  @GetMapping(value = "/order/inventory/{productId}", produces = "application/json")
  Mono<Inventory> getOrderInventory(@PathVariable(value = "productId") Long productId);

  @GetMapping(value = "/orders", produces = "application/json")
  Mono<List<PurchaseOrder>> getOrders(@RequestParam(name = "type", defaultValue = "ALL") GetOrdersType type); 

}
