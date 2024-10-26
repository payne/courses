import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { InputRowComponent } from 'app/common/input-row/input-row.component';
import { StudentService } from 'app/student/student.service';
import { StudentDTO } from 'app/student/student.model';
import { ErrorHandler } from 'app/common/error-handler.injectable';


@Component({
  selector: 'app-student-add',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, InputRowComponent],
  templateUrl: './student-add.component.html'
})
export class StudentAddComponent implements OnInit {

  studentService = inject(StudentService);
  router = inject(Router);
  errorHandler = inject(ErrorHandler);

  coursesValues?: Map<number,string>;

  addForm = new FormGroup({
    name: new FormControl(null, [Validators.required, Validators.maxLength(255)]),
    email: new FormControl(null, [Validators.required, Validators.maxLength(255)]),
    courses: new FormControl([])
  }, { updateOn: 'submit' });

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      created: $localize`:@@student.create.success:Student was created successfully.`
    };
    return messages[key];
  }

  ngOnInit() {
    this.studentService.getCoursesValues()
        .subscribe({
          next: (data) => this.coursesValues = data,
          error: (error) => this.errorHandler.handleServerError(error.error)
        });
  }

  handleSubmit() {
    window.scrollTo(0, 0);
    this.addForm.markAllAsTouched();
    if (!this.addForm.valid) {
      return;
    }
    const data = new StudentDTO(this.addForm.value);
    this.studentService.createStudent(data)
        .subscribe({
          next: () => this.router.navigate(['/students'], {
            state: {
              msgSuccess: this.getMessage('created')
            }
          }),
          error: (error) => this.errorHandler.handleServerError(error.error, this.addForm, this.getMessage)
        });
  }

}
