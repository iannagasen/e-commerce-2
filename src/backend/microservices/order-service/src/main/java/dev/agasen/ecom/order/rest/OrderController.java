package dev.agasen.ecom.order.rest;

import org.springframework.web.bind.annotation.RestController;

import dev.agasen.ecom.api.core.order.model.CreateOrderRequest;
import dev.agasen.ecom.api.core.order.model.PurchaseOrder;
import dev.agasen.ecom.order.OrderService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
  
  private final OrderService orderService;


  @PostMapping
  public Mono<PurchaseOrder> placeOrder(@RequestBody Mono<CreateOrderRequest> req) {
      return req.flatMap(orderService::placeOrder)
        .cast(PurchaseOrder.class)
        .doFirst(() -> System.out.println("RECEIVVVVVVEEEED!!!!!"))
        .doOnError(order -> System.out.println("ERROR!!! " + order));
  }
  

}
