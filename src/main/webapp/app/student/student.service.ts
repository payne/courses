import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';
import { StudentDTO } from 'app/student/student.model';
import { PagedModel } from 'app/common/list-helper/pagination.component';
import { map } from 'rxjs';
import { transformRecordToMap } from 'app/common/utils';


@Injectable({
  providedIn: 'root',
})
export class StudentService {

  http = inject(HttpClient);
  resourcePath = environment.apiPath + '/api/students';

  getAllStudents(params?: Record<string,string>) {
    return this.http.get<PagedModel<StudentDTO>>(this.resourcePath, { params });
  }

  getStudent(id: number) {
    return this.http.get<StudentDTO>(this.resourcePath + '/' + id);
  }

  createStudent(studentDTO: StudentDTO) {
    return this.http.post<number>(this.resourcePath, studentDTO);
  }

  updateStudent(id: number, studentDTO: StudentDTO) {
    return this.http.put<number>(this.resourcePath + '/' + id, studentDTO);
  }

  deleteStudent(id: number) {
    return this.http.delete(this.resourcePath + '/' + id);
  }

  getCoursesValues() {
    return this.http.get<Record<string,string>>(this.resourcePath + '/coursesValues')
        .pipe(map(transformRecordToMap));
  }

}
