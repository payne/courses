import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormControl, FormGroup, Validators } from '@angular/forms';
import { InputRowComponent } from 'app/common/input-row/input-row.component';
import { CourseService } from 'app/course/course.service';
import { CourseDTO } from 'app/course/course.model';
import { ErrorHandler } from 'app/common/error-handler.injectable';


@Component({
  selector: 'app-course-add',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, InputRowComponent],
  templateUrl: './course-add.component.html'
})
export class CourseAddComponent {

  courseService = inject(CourseService);
  router = inject(Router);
  errorHandler = inject(ErrorHandler);

  addForm = new FormGroup({
    name: new FormControl(null, [Validators.required, Validators.maxLength(255)]),
    code: new FormControl(null, [Validators.required, Validators.maxLength(255)]),
    credits: new FormControl(null, [Validators.required])
  }, { updateOn: 'submit' });

  getMessage(key: string, details?: any) {
    const messages: Record<string, string> = {
      created: $localize`:@@course.create.success:Course was created successfully.`,
      COURSE_CODE_UNIQUE: $localize`:@@Exists.course.code:This Code is already taken.`
    };
    return messages[key];
  }

  handleSubmit() {
    window.scrollTo(0, 0);
    this.addForm.markAllAsTouched();
    if (!this.addForm.valid) {
      return;
    }
    const data = new CourseDTO(this.addForm.value);
    this.courseService.createCourse(data)
        .subscribe({
          next: () => this.router.navigate(['/courses'], {
            state: {
              msgSuccess: this.getMessage('created')
            }
          }),
          error: (error) => this.errorHandler.handleServerError(error.error, this.addForm, this.getMessage)
        });
  }

}
