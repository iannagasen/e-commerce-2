import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { OrderControllerService } from '../api/orderservice/services';
import { PurchaseOrder } from '../api/orderservice/models';

@Component({
  selector: 'app-all-orders',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>All Orders</h1>
    <p>Here are all the orders</p>
    <ul>
      <li *ngFor="let order of allOrders$ | async">
        <!-- Display properties of order here, for example: -->
        Order ID: {{ order.orderId }}, Order Name: {{ order.customerId }}
      </li>
    </ul>
  `
})
export class AllOrdersComponent {
  
  allOrders$!: Observable<Array<PurchaseOrder>>;

  constructor(
    private _orderControllerService: OrderControllerService
  ) { }

  ngOnInit(): void {
    this.allOrders$ = this._orderControllerService.getOrders();
  }

}
