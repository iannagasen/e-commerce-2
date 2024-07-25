package dev.agasen.ecom.order;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@ComponentScan("dev.agasen")
@RestController
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@GetMapping("/demo") 
	public Authentication demo(Authentication a) {
		return a;
	}

	@GetMapping("/public")
	public Map<String, String> publicEndpoint() {
		return Map.of("data", "this is a public endpoint");
	}
	

}
