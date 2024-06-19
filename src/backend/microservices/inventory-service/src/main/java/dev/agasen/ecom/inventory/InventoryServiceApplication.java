package dev.agasen.ecom.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("dev.agasen")
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	// @Bean
	// public CommandLineRunner run(InventoryRepository inventoryRepo, InventoryUpdateRepository inventoryHistoRepo) {
	// 	return args -> {
	// 		// inventoryRepo.save(new InventoryEntity("1", 1L, 10)).block();
	// 		// inventoryRepo.save(new InventoryEntity("1", 2L, 10)).block();
	// 	};
	// }

}
