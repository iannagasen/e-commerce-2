package dev.agasen.ecom.order;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.agasen.ecom.order.auth.resourceserver.EcomAuthentication;
import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@ComponentScan("dev.agasen")
@RestController
@Slf4j
 class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@GetMapping("/demo") 
	public Authentication demo(Authentication a) {
		return a;
	}

	@GetMapping("/public")
	public Map<String, String> publicEndpoint(@AuthenticationPrincipal EcomAuthentication customAuthentication) {
		log.info("Custom Authentication: " + customAuthentication);
		return Map.of("data", "this is a public endpoint");
	}
	

}
