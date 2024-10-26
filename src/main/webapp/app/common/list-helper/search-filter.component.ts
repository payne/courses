import { CommonModule } from '@angular/common';
import { Component, inject, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';


@Component({
  selector: 'app-search-filter',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './search-filter.component.html'
})
export class SearchFilterComponent implements OnInit {

  @Input()
  placeholder?: string;

  route = inject(ActivatedRoute);
  router = inject(Router);
  filterForm = new FormGroup({
    filter: new FormControl()
  });

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      const activeFilter = this.route.snapshot.queryParamMap.get('filter');
      const filterControl = this.filterForm.get('filter')!;
      filterControl.setValue(activeFilter);
    });
  }

  handleSubmit() {
    if (!this.filterForm.valid) {
      return;
    }
    const filterControl = this.filterForm.get('filter')!;
    const queryParams:Record<string,string> = {
      filter: filterControl.value
    };
    this.router.navigate([window.location.pathname], { queryParams });
  }

}
