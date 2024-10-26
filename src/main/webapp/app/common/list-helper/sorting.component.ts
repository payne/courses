import { CommonModule, KeyValuePipe } from '@angular/common';
import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-sorting',
  standalone: true,
  imports: [CommonModule, KeyValuePipe, ReactiveFormsModule],
  templateUrl: './sorting.component.html'
})
export class SortingComponent implements OnInit {

  @Input({ required: true })
  sortOptions?: Record<string,string>;

  route = inject(ActivatedRoute);
  router = inject(Router);
  dropdown = new FormControl();

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      const activeSorting = this.route.snapshot.queryParamMap.get('sort') || Object.keys(this.sortOptions!)[0];
      this.dropdown.setValue(activeSorting);
    });
  }

  handleChange() {
    const queryParams:Record<string,string> = {
      sort: this.dropdown.value
    };
    const filter = this.route.snapshot.queryParamMap.get('filter');
    if (filter) {
      queryParams['filter'] = filter;
    }
    this.router.navigate([window.location.pathname], { queryParams });
  }

}
