import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { CourseDTO } from 'app/course/course.model';
import { PagedModel } from 'app/common/list-helper/pagination.component';


@Injectable({
  providedIn: 'root',
})
export class CourseService {

  http = inject(HttpClient);
  resourcePath = environment.apiPath + '/api/courses';

  getAllCourses(params?: Record<string,string>) {
    return this.http.get<PagedModel<CourseDTO>>(this.resourcePath, { params });
  }

  getCourse(id: number) {
    return this.http.get<CourseDTO>(this.resourcePath + '/' + id);
  }

  createCourse(courseDTO: CourseDTO) {
    return this.http.post<number>(this.resourcePath, courseDTO);
  }

  updateCourse(id: number, courseDTO: CourseDTO) {
    return this.http.put<number>(this.resourcePath + '/' + id, courseDTO);
  }

  deleteCourse(id: number) {
    return this.http.delete(this.resourcePath + '/' + id);
  }

}
