package dev.agasen.ecom.api.core.inventory.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public interface Inventory {

  Long getInventoryId();
  void setInventoryId(Long id);

  Long getProductId();
  void setProductId(Long productId);

  int getStock();
  void setStock(int stock);

  @JsonInclude(JsonInclude.Include.NON_NULL) List<InventoryUpdate> getHistory();
  void setHistory(List<InventoryUpdate> history);

}
