// create a mongo db
inventory_db = db.getSiblingDB('inventory-db');

inventory_db.createCollection('inventory');

inventory_db.inventory.insertMany([
  {
    inventoryId: 1,
    productId: 1,
    stock: 100,
  },
  {
    inventoryId: 2,
    productId: 2,
    stock: 200,
  },
  {
    inventoryId: 3,
    productId: 3,
    stock: 300,
  }
]);

inventory_db.createCollection('inventory_updates');

inventory_db.inventory_updates.insertMany([
  {
    updateId: 1,
    inventoryId: 1,
    orderId: 1,
    quantity: 10,
    type: 'PURCHASE',
    createdAt: new Date(),
  }
]);
