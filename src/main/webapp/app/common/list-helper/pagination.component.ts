import { CommonModule } from '@angular/common';
import { Component, inject, Input, OnChanges, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';


@Component({
  selector: 'app-pagination',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pagination.component.html'
})
export class PaginationComponent implements OnChanges, OnInit {

  @Input({ required: true })
  page?: Page;

  route = inject(ActivatedRoute);
  pathname = window.location.pathname;

  steps: PaginationStep[] = [];
  range = '';

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      previous: $localize`:@@pagination.previous:Previous`,
      next: $localize`:@@pagination.next:Next`
    };
    return messages[key];
  }

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      if (params['page'] || params['size']) {
        this.initViewData();
      }
    });
  }

  ngOnChanges() {
    this.initViewData();
  }

  initViewData() {
    const page = this.page!;

    this.steps = [];
    const previous = new PaginationStep();
    previous.disabled = page.number === 0;
    previous.label = this.getMessage('previous');
    previous.params = this.getStepParams(Math.max(0, page.number - 1));
    this.steps.push(previous);
    // find a range of up to 5 pages around the current active page
    const startAt = Math.max(0, Math.min(page.number - 2, page.totalPages - 5));
    const endAt = Math.min(startAt + 5, page.totalPages);
    for (let i = startAt; i < endAt; i++) {
      const step = new PaginationStep();
      step.active = i == page.number;
      step.label = '' + (i + 1);
      step.params = this.getStepParams(i);
      this.steps.push(step);
    }
    const next = new PaginationStep();
    next.disabled = page.number > page.totalPages - 2;
    next.label = this.getMessage('next');
    next.params = this.getStepParams(Math.min(page.totalPages - 1, page.number + 1));
    this.steps.push(next);

    const rangeStart = page.number * page.size + 1;
    const rangeEnd = Math.min(rangeStart + page.size - 1, page.totalElements);
    if (rangeStart === rangeEnd) {
      this.range = '' + rangeStart;
    } else {
      this.range = rangeStart + '-' + rangeEnd;
    }
  }

  getStepParams(targetPage:number) {
    const params: Record<string, any> = {
      page: targetPage,
      size: this.page!.size
    };
    const sort = this.route.snapshot.queryParamMap.get('sort')
    if (sort) {
      params['sort'] = sort;
    }
    const filter = this.route.snapshot.queryParamMap.get('filter');
    if (filter) {
      params['filter'] = filter;
    }
    return params;
  }

}

export interface Page {

  totalElements: number;
  totalPages: number;
  number: number;
  size: number;

}

export interface PagedModel<PageType> {

  content: PageType[];
  page: Page;

}

class PaginationStep {

  active = false;
  disabled = false;
  label = '';
  params:Record<string,any> = {};

}
