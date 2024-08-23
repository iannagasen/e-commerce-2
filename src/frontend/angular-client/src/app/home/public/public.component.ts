import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Category } from './model/category';
import { DUMMY_CATEGORIES } from './dummy';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-public',
  standalone: true,
  imports: [CommonModule],
  template: `
<div>Public Component</div>

<div class="border-4 border-green-500">
  <div>Flash Sale</div>
</div>

<div class="border-4 border-red-500 mt-2">
  <div>Categories</div>
  <div class="flex flex-wrap justify-center">
    <div *ngFor="let category of categories$ | async" (click)="handleClickCategory(category)">
      <div class="border border-slate-400 m-2 w-32 h-32 cursor-pointer hover:shadow-red">
        <div>{{category.name}}</div>
        <div>{{category.description}}</div>
        <img [src]="category.imageUrl" alt="category image" />
      </div>
    </div>
  </div>
`  
})
export class PublicComponent implements OnInit {

  categories$!: Observable<Category[]>;

  constructor(
    private _router: Router,
    private _http: HttpClient
  ) { }
  
  ngOnInit(): void {
    this.categories$ = of(DUMMY_CATEGORIES)
    this._http.get('http://localhost:8103/public').subscribe(console.log)
  }

  handleClickCategory(category: Category) {
    console.log("Category clicked: " + category.name);

    // Navigate to category page
    this._router.navigate(['/public/category', category.id]);
  }
      
}
