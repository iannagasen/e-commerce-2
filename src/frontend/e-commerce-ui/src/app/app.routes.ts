import { Routes } from "@angular/router";
import { AllOrdersComponent } from "./all-orders/all-orders.component";

export const routes: Routes = [
  {
    path: 'all-orders',
    title: 'All Orders',
    component: AllOrdersComponent,
  },
];