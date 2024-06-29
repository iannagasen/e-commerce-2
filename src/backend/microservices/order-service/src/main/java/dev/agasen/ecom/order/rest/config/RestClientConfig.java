package dev.agasen.ecom.order.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import dev.agasen.ecom.api.core.inventory.rest.InventoryServiceProxy;


@Configuration
public class RestClientConfig {

  private @Value("${app.inventory.host}") String inventoryServiceHost;
  private @Value("${app.payment.host}") String paymentServiceHost;

  // @Bean
  // public WebClient.Builder loadBalancedWebClientBuilder() {
  //   // SHOULD BE ANNOTATED WITH @LoadBalanced IF USING NETFLIX EUREKA
  //   return WebClient.builder();
  // }

  @Bean
  public InventoryServiceProxy inventoryServiceProxy() {
    WebClient webClient = WebClient.builder()
        .baseUrl(inventoryServiceHost)
        .build();
    
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(WebClientAdapter.create(webClient))
        .build();

    return factory.createClient(InventoryServiceProxy.class);
  }



}
