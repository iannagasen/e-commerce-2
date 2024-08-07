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

payment_db = db.getSiblingDB('payment-db');

payment_db.createCollection('balance');
payment_db.balance.insertMany([
  {
    balanceId: 1,
    customerId: 1,
    amount: 10000
  },
  {
    balanceId: 2,
    customerId: 2,
    amount: 20000
  },
  {
    balanceId: 3,
    customerId: 3,
    amount: 30000
  }
]);

payment_db.createCollection('payment');
payment_db.payment.insertMany([
  {
    paymentId: 1,
    orderId: 1,
    customerId: 1,
    orderItems:[
      {
        productId: 1,
        quantity: 10,
        price: 100
      },
      {
        productId: 2,
        quantity: 20,
        price: 200
      }
    ],
    transactionType: 'PAYMENT',
    createdAt: new Date(),
  }
]);

order_db = db.getSiblingDB('order-db');
order_db.createCollection('purchase_order');

order_db.purchase_order.insertMany([
  {
    "orderId": 1,
    "customerId": 2,
    "items": [
      {
        "productId": 2,
        "quantity": 300,
        "price": 10
      },
      {
        "productId": 2,
        "quantity": 2,
        "price": 10
      }
    ],
    "orderStatus": "PENDING"
  },
  
  {
    "orderId": 2,
    "customerId": 2,
    "items": [
      {
        "productId": 2,
        "quantity": 300,
        "price": 10
      },
      {
        "productId": 2,
        "quantity": 2,
        "price": 10
      }
    ],
    "orderStatus": "PENDING"
  },

  
  {
    "orderId": 3,
    "customerId": 2,
    "items": [
      {
        "productId": 2,
        "quantity": 300,
        "price": 10
      },
      {
        "productId": 2,
        "quantity": 2,
        "price": 10
      }
    ],
    "orderStatus": "PENDING"
  }
  
]);