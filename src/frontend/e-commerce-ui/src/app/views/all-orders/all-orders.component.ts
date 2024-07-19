import { Component } from '@angular/core';
import { OrderControllerService } from '../../api/services/order-controller.service';
import { Observable } from 'rxjs';
import { OrderDetails } from 'src/app/api/models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-all-orders',
  template: `
  <p>template is the shit</p>
  <!-- <div *ngIf="_orderDetails$ | async as orderDetails">
      <div *ngFor="let order of orderDetails">
        <p>{{ order.totalPayment }}</p> 
      </div>
    </div>
    <p *ngIf="!(_orderDetails$ | async)">Loading orders...</p> -->
  `
})
export class AllOrdersComponent {

  // _orderDetails$: Observable<Array<OrderDetails>>

  constructor(
    private _orderControllerService: OrderControllerService,
    private _router: Router
  ) {
    // this._orderDetails$ = orderControllerService.getOrders();
  }
}
