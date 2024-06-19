package dev.agasen.ecom.api.core.inventory.model;

public enum InventoryUpdateType {
  
  PURCHASE("Stock deducted due to customer purchase"),
  RESTOCK("Stock added during restocking"),
  CUSTOMER_RETURN("Stock increased due to customer return"),
  DAMAGED("Stock deducted due to damage"),
  INVENTORY_ADJUSTMENT("Stock adjusted during inventory audit"),
  SUPPLIER_RETURN("Stock decreased due to return to supplier");

  private final String description;

  InventoryUpdateType(String description) {
      this.description = description;
  }

  public String getDescription() {
      return description;
  }
}
