import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-root',
  styleUrls: ['./app.component.scss'],
  imports: [RouterOutlet, CommonModule],
  template: `
    <router-outlet></router-outlet>
  ` 
})
export class AppComponent {
  title = 'e-commerce-ui';
}
