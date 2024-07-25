import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './api/authservice/auth.service';
import { AuthenticationService } from './auth/service/authentication.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,CommonModule],
  styleUrl: './app.component.scss',
  template: `
    <div>{{isLoggedIn ? 'User Logged in' : 'No user Logged in'}}</div>
  `

})
export class AppComponent implements OnInit {

  public isLoggedIn = false;
  

  constructor(
    private auth: AuthenticationService,
    private route: ActivatedRoute
  ) { }
  
  ngOnInit(): void {
    this.isLoggedIn = this.auth.isLoggedIn();
    console.log(this.isLoggedIn);

  }
  

}
