/* tslint:disable */
/* eslint-disable */
import { OrderItem } from '../models/order-item';
export interface PurchaseOrder {
  customerId?: number;
  items?: Array<OrderItem>;
  orderId?: number;
  orderStatus?: 'PENDING' | 'COMPLETED' | 'CANCELLED';
}
