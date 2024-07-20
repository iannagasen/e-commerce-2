export * from './orderController.service';
import { OrderControllerService } from './orderController.service';
export * from './testController.service';
import { TestControllerService } from './testController.service';
export const APIS = [OrderControllerService, TestControllerService];
