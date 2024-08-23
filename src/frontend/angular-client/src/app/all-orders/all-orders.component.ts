import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AllOrdersService } from './all-orders.service';
import { PurchaseOrder } from './model/purchase-order';

@Component({
  selector: 'app-all-orders',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h1>All Orders</h1>
    <p>Here are all the orders</p>
    <button (click)="getAllOrders()">get all</button>
    <ul>
      <!-- <li *ngFor="let order of allOrders$ | async">
        Order ID: {{ order.orderId }}, Order Name: {{ order.customerId }}
      </li> -->
    </ul>
  `
})
export class AllOrdersComponent {
  
  allOrders$!: Observable<Array<PurchaseOrder>>;

  constructor(
    private _orderControllerService: AllOrdersService

  ) { }

  ngOnInit(): void {
    // this._orderControllerService.getAllOrders().subscribe(console.log);
  }

  public getAllOrders() {
    this._orderControllerService.getAllOrders().subscribe(console.log);
  }

}
