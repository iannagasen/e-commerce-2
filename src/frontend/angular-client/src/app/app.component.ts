import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from './auth/service/authentication.service';
import { HttpClient } from '@angular/common/http';
import { EMPTY, map, merge, mergeMap, Observable, of, tap } from 'rxjs';
import { fromPromise } from 'rxjs/internal/observable/innerFrom';
import { PublicComponent } from './home/public/public.component';
import { PublicCategoryComponent } from './category/public-category/public-category.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule, 
    PublicComponent, 
    PublicCategoryComponent
  ],
  styleUrl: './app.component.scss',
  template: `
    <div *ngIf="authStatus$ | async as status">
      <!-- <ng-template [ngIf]="isHome$ | async"> -->
        <div>{{status.isLoggedIn ? 'User Logged in' : 'No user Logged in'}}</div>
        
        <div *ngIf="!status.isLoggedIn">
          <div>Message from backend</div>
          <button (click)="loginViaOauth2()">Login using oauth2</button>
        </div>
      <!-- </ng-template> -->
    </div>
    <router-outlet />
  `

})
export class AppComponent implements OnInit {

  authStatus$!: Observable<{ isLoggedIn: boolean}>;
  isHome$!: Observable<boolean>;

  constructor(
    private auth: AuthenticationService,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) { }
  
  ngOnInit(): void {
    this.authStatus$ = this.checkLoggedInStatus();
    // this.isHome$ = this.checkIfHome();
  }

  loginViaOauth2() {
    this.auth.redirectToOauthLogin();
  }

  private checkLoggedInStatus() {
    /**
     * 1. Check logged in status
     * 2. If not logged in, check if there is a code in the query params (since we may be redirected from the oauth2 server)
     * 3. If there is a code, exchange it for a token
     */
    return this.auth.isLoggedIn().pipe(
      tap(data => console.log("Is user logged in: " + data)),
      mergeMap(loggedIn => {
        console.log("RUNNING");
        if (loggedIn) {
          return of(true);
        } else {
          return this.route.queryParams.pipe(
            mergeMap(params => { 
              if (params?.['code']) {
                return this.auth.exchangeCodeForToken(params['code']).pipe(
                  mergeMap((_) => fromPromise(this.router.navigate([],{
                    queryParams: { code: null, state: null },
                    queryParamsHandling: 'merge',
                    replaceUrl: true
                  }))),
                  map(() => true));
              } else {
                return of(false);
              } 
            })
          );
        }
      }),
      map(isLoggedIn => ({ isLoggedIn }))
    )
  }

  private checkIfHome() {
    return this.router.events.pipe(
      map(event => event instanceof NavigationEnd && event.url === '/'),
    )
  }


}
