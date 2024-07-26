import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-public-category',
  standalone: true,
  imports: [CommonModule],
  template: `
  <div>Public Category Component: {{categoryId}}</div>
`

})
export class PublicCategoryComponent {
  categoryId!: string;

  constructor(
    private _route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.categoryId = this._route.snapshot.paramMap.get('id')!;
  }

}
