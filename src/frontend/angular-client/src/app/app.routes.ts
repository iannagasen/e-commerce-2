import { Routes } from '@angular/router';
import { AllOrdersComponent } from './all-orders/all-orders.component';
import { Category } from './home/public/model/category';
import { PublicCategoryComponent } from './category/public-category/public-category.component';
import { PublicComponent } from './home/public/public.component';

export const routes: Routes = [
  {
    title: 'Home',
    path: '',
    component: PublicComponent
  },
  {
    title: 'All Orders',
    path: 'all-orders',
    component: AllOrdersComponent
  },
  {
    title: 'Shop by Category',
    path: 'public/category/:id',
    component: PublicCategoryComponent
  },
  {
    title: 'Orders',
    path: 'orders',
    component: AllOrdersComponent

  },


];
