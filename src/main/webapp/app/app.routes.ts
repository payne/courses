import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { StudentListComponent } from './student/student-list.component';
import { StudentAddComponent } from './student/student-add.component';
import { StudentEditComponent } from './student/student-edit.component';
import { CourseListComponent } from './course/course-list.component';
import { CourseAddComponent } from './course/course-add.component';
import { CourseEditComponent } from './course/course-edit.component';
import { ErrorComponent } from './error/error.component';


export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    title: $localize`:@@home.index.headline:Welcome to your new app!`
  },
  {
    path: 'students',
    component: StudentListComponent,
    title: $localize`:@@student.list.headline:Students`
  },
  {
    path: 'students/add',
    component: StudentAddComponent,
    title: $localize`:@@student.add.headline:Add Student`
  },
  {
    path: 'students/edit/:id',
    component: StudentEditComponent,
    title: $localize`:@@student.edit.headline:Edit Student`
  },
  {
    path: 'courses',
    component: CourseListComponent,
    title: $localize`:@@course.list.headline:Courses`
  },
  {
    path: 'courses/add',
    component: CourseAddComponent,
    title: $localize`:@@course.add.headline:Add Course`
  },
  {
    path: 'courses/edit/:id',
    component: CourseEditComponent,
    title: $localize`:@@course.edit.headline:Edit Course`
  },
  {
    path: 'error',
    component: ErrorComponent,
    title: $localize`:@@error.headline:Error`
  },
  {
    path: '**',
    component: ErrorComponent,
    title: $localize`:@@notFound.headline:Page not found`
  }
];
