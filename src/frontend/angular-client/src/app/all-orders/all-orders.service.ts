import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PurchaseOrder } from './model/purchase-order';
import { ACCESS_TOKEN } from '../auth/service/authentication.service';

@Injectable({
  providedIn: 'root'
})
export class AllOrdersService {

  constructor(
    private _http: HttpClient,
  ) { }

  getAllOrders() {
    const accessToken = localStorage.getItem(ACCESS_TOKEN);
    console.log("Access Token" + accessToken);
    
    return this._http.get<PurchaseOrder[]>('http://localhost:8103/orders');
  }
}
