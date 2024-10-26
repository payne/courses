import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { ErrorHandler } from 'app/common/error-handler.injectable';
import { CourseService } from 'app/course/course.service';
import { CourseDTO } from 'app/course/course.model';
import { SearchFilterComponent } from 'app/common/list-helper/search-filter.component';
import { SortingComponent } from 'app/common/list-helper/sorting.component';
import { getListParams } from 'app/common/utils';
import { PagedModel, PaginationComponent } from 'app/common/list-helper/pagination.component';


@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, SearchFilterComponent ,SortingComponent, PaginationComponent, RouterLink],
  templateUrl: './course-list.component.html'})
export class CourseListComponent implements OnInit, OnDestroy {

  courseService = inject(CourseService);
  errorHandler = inject(ErrorHandler);
  route = inject(ActivatedRoute);
  router = inject(Router);
  courses?: PagedModel<CourseDTO>;
  navigationSubscription?: Subscription;

  sortOptions = {
    'id,ASC': $localize`:@@course.list.sort.id,ASC:Sort by Id (Ascending)`, 
    'name,ASC': $localize`:@@course.list.sort.name,ASC:Sort by Name (Ascending)`, 
    'code,ASC': $localize`:@@course.list.sort.code,ASC:Sort by Code (Ascending)`
  }

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      confirm: $localize`:@@delete.confirm:Do you really want to delete this element? This cannot be undone.`,
      deleted: $localize`:@@course.delete.success:Course was removed successfully.`    };
    return messages[key];
  }

  ngOnInit() {
    this.loadData();
    this.navigationSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.loadData();
      }
    });
  }

  ngOnDestroy() {
    this.navigationSubscription!.unsubscribe();
  }
  
  loadData() {
    this.courseService.getAllCourses(getListParams(this.route))
        .subscribe({
          next: (data) => this.courses = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  confirmDelete(id: number) {
    if (confirm(this.getMessage('confirm'))) {
      this.courseService.deleteCourse(id)
          .subscribe({
            next: () => this.router.navigate(['/courses'], {
              state: {
                msgInfo: this.getMessage('deleted')
              }
            }),
            error: (error) => this.errorHandler.handleServerError(error.error)
          });
    }
  }

}
