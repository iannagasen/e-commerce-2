import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { ACCESS_TOKEN } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(
    private route: Router,
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    console.log('Checking if user is logged in')
    const accessToken = localStorage.getItem(ACCESS_TOKEN);
    if (accessToken === null || accessToken === '') {
      this.route.navigate(['/']);
      return false;
    } else {
      return true;
    }

  }
}

// export const AuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
//   return inject(PermissionService).canActivate(route, state)
// }